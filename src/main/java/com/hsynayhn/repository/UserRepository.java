package com.hsynayhn.repository;

import com.hsynayhn.entity.User;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByTckn(@NotEmpty String tckn);

    boolean existsByEmail(@NotEmpty String email);

    Optional findById(UUID id);
}
