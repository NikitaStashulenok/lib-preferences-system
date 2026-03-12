package com.example.library.service;

import com.example.library.dto.BookDtos;
import com.example.library.model.Book;
import com.example.library.model.LoanStatus;
import com.example.library.model.Rating;
import com.example.library.model.RecommendationProfile;
import com.example.library.repository.BookRepository;
import com.example.library.repository.LoanRepository;
import com.example.library.repository.RatingRepository;
import com.example.library.repository.RecommendationProfileRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecommendationService {
    private final RecommendationProfileRepository profileRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final CurrentUserService currentUserService;
    private final RatingRepository ratingRepository;
    private final LoanRepository loanRepository;

    public RecommendationService(RecommendationProfileRepository profileRepository,
                                 BookRepository bookRepository,
                                 BookService bookService,
                                 CurrentUserService currentUserService,
                                 RatingRepository ratingRepository,
                                 LoanRepository loanRepository) {
        this.profileRepository = profileRepository;
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.currentUserService = currentUserService;
        this.ratingRepository = ratingRepository;
        this.loanRepository = loanRepository;
    }

    @Transactional(readOnly = true)
    public Page<BookDtos.RecommendationResponse> getRecommendations(Long userId, int page, int size, String source) {
        currentUserService.requireSameUserOrAdmin(userId);

        RecommendationProfile profile = profileRepository.findByUserId(userId).orElse(null);
        Set<String> userGenres = parseCsv(profile == null ? null : profile.getPreferredGenresCsv());
        Set<String> userAuthors = parseCsv(profile == null ? null : profile.getFavoriteAuthorsCsv());

        Set<String> systemGenres = new LinkedHashSet<>();
        Set<String> systemAuthors = new LinkedHashSet<>();
        for (Rating rating : ratingRepository.findByUserIdAndScoreGreaterThanEqual(userId, 4)) {
            Long bookId = rating.getBook().getId();
            boolean returned = loanRepository.existsByUserIdAndBookIdAndStatus(userId, bookId, LoanStatus.RETURNED);
            if (!returned) continue;

            systemAuthors.add(normalize(rating.getBook().getAuthor()));
            parseCsv(rating.getBook().getGenresCsv()).forEach(systemGenres::add);
        }

        List<BookDtos.RecommendationResponse> candidates = new ArrayList<>();
        for (Book book : bookRepository.findAll()) {
            List<String> tags = new ArrayList<>();
            boolean userMatch = matchesBook(book, userGenres, userAuthors);
            boolean systemMatch = matchesBook(book, systemGenres, systemAuthors);

            if (userMatch) tags.add("USER");
            if (systemMatch) tags.add("SYSTEM");
            if (tags.isEmpty()) continue;

            if ("system".equalsIgnoreCase(source) && !tags.contains("SYSTEM")) continue;
            if ("user".equalsIgnoreCase(source) && !tags.contains("USER")) continue;

            candidates.add(new BookDtos.RecommendationResponse(bookService.getById(book.getId()), tags));
        }

        candidates.sort((a, b) -> {
            int byTags = Integer.compare(b.sourceTags().size(), a.sourceTags().size());
            if (byTags != 0) return byTags;
            return a.book().title().compareToIgnoreCase(b.book().title());
        });

        int from = Math.min(page * size, candidates.size());
        int to = Math.min(from + size, candidates.size());
        List<BookDtos.RecommendationResponse> content = candidates.subList(from, to);
        return new PageImpl<>(content, PageRequest.of(page, size), candidates.size());
    }

    private boolean matchesBook(Book book, Set<String> genres, Set<String> authors) {
        if (genres.isEmpty() && authors.isEmpty()) {
            return false;
        }
        String author = normalize(book.getAuthor());
        Set<String> bookGenres = parseCsv(book.getGenresCsv());

        boolean authorMatch = !authors.isEmpty() && authors.stream().anyMatch(a -> !a.isBlank() && author.contains(a));
        boolean genreMatch = !genres.isEmpty() && genres.stream().anyMatch(g -> bookGenres.stream().anyMatch(bg -> bg.contains(g)));
        return authorMatch || genreMatch;
    }

    private Set<String> parseCsv(String csv) {
        if (csv == null || csv.isBlank()) return Set.of();
        Set<String> result = new LinkedHashSet<>();
        Arrays.stream(csv.split(","))
                .map(this::normalize)
                .filter(s -> !s.isBlank())
                .forEach(result::add);
        return result;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
