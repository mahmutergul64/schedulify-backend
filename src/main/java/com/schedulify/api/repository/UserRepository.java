package com.schedulify.api.repository;

import com.schedulify.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.role = 'ROLE_PROVIDER'")
    List<User> findAllProviders();

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(String role);

    @Query("SELECT u FROM User u WHERE u.role = 'ROLE_PROVIDER' AND " +
            "(LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.specialty) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<User> searchProviders(@Param("keyword") String keyword);

    Optional<User> findByResetPasswordToken(String token);
}