package com.example.library.repository;

import com.example.library.model.Loan;
import com.example.library.model.LoanStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserIdOrderByBorrowedAtDesc(Long userId);
    long countByUserIdAndStatus(Long userId, LoanStatus status);
}
