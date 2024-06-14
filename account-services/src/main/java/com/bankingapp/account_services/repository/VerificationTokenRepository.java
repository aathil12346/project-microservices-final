package com.bankingapp.account_services.repository;

import com.bankingapp.account_services.entity.User;
import com.bankingapp.account_services.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long> {

    VerificationToken findByToken(String token);

    VerificationToken findFirstByUserOrderByCreatedDateDesc(User user);
}
