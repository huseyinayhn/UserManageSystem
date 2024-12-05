package com.hsynayhn.service.impl;

import com.hsynayhn.entity.EmailVerification;
import com.hsynayhn.repository.VerificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final VerificationRepository verificationRepository;

    public EmailService(JavaMailSender mailSender, VerificationRepository verificationRepository) {
        this.mailSender = mailSender;
        this.verificationRepository = verificationRepository;
    }

    public String generateCode(int length) {
            SecureRandom random = new SecureRandom();
            StringBuilder code = new StringBuilder();

            for (int i = 0; i < length; i++) {
                code.append(random.nextInt(10));
            }
            return code.toString();
    }

    public boolean verifyCode(String email, String inputCode) {
        EmailVerification verification = verificationRepository.findByEmail(email);

        if (verification == null) {
            throw new RuntimeException("Verification record not found.");
        }

        if (verification.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification code has expired.");
        }

        return verification.getVerificationCode().equals(inputCode);
    }

    public void sendVerificationEmail(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            System.out.println(to);

            helper.setTo(to);
            helper.setSubject("Your Verification Code");
            helper.setText("Your verification code is: " + code, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
