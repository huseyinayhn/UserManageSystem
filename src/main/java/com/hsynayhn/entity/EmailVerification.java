package com.hsynayhn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Entity
@Table(name = "email_verifications")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "verification_code", nullable = false)
    private String verificationCode;

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;


    public EmailVerification(String email, String verificationCode, LocalDateTime expirationTime) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.expirationTime = expirationTime;
    }
}
