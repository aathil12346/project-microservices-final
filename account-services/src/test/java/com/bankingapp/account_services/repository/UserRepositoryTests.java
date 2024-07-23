package com.bankingapp.account_services.repository;

import com.bankingapp.account_services.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class UserRepositoryTests {

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
    public void findUserByEmail(){

        User savedUser = userRepository.save(user);

        Optional<User> retrievedUser = userRepository.findByEmail(savedUser.getEmail());

        if (retrievedUser.isPresent()){

            User finalUser = retrievedUser.get();

            Assertions.assertThat(finalUser.getEmail()).isEqualTo(savedUser.getEmail());
        }

    }
}
