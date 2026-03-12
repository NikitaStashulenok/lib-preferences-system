package com.example.library.repository;

import com.example.library.model.User;
import com.example.library.model.Role;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("""
            select distinct u from User u left join u.roles r
            where (:query is null or :query = ''
                  or lower(u.email) like lower(concat('%', :query, '%'))
                  or lower(coalesce(u.nickname, '')) like lower(concat('%', :query, '%')))
              and (:role is null or r = :role)
            """)
    Page<User> searchAdmin(@Param("query") String query, @Param("role") Role role, Pageable pageable);
}
