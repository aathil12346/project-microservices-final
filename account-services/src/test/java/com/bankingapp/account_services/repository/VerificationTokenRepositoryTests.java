package com.bankingapp.account_services.repository;

import com.bankingapp.account_services.entity.User;
import com.bankingapp.account_services.entity.VerificationToken;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
public class VerificationTokenRepositoryTests {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp(){
        user = User.builder()
                .firstname("Aathil")
                .lastname("Meeran")
                .email("aathilmeeran46@gmail.com")
                .isEmailVerified(false)
                .role("ROLE_USER")
                .postCode("G40NT")
                .streetName("St James Road")
                .password("Aathil@123")
                .build();
    }

    @Test
    public void findByToken(){

        User savedUser = userRepository.save(user);

        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = VerificationToken.builder()
                .user(savedUser)
                .expiryDate(LocalDateTime.now().plusHours(24L))
                .token(token)
                .createdDate(LocalDateTime.now())
                .build();

        VerificationToken savedToken = verificationTokenRepository.save(verificationToken);

        VerificationToken retrievedToken = verificationTokenRepository.findByToken(savedToken.getToken());

        Assertions.assertThat(retrievedToken).isNotNull();

    }
}
