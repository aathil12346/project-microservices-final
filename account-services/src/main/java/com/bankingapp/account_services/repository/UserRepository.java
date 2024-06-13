package com.bankingapp.account_services.repository;

import com.bankingapp.account_services.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
