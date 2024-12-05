package com.hsynayhn.repository;

import com.hsynayhn.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationRepository extends JpaRepository<EmailVerification, Long> {
    EmailVerification findByEmail(String email);
}
