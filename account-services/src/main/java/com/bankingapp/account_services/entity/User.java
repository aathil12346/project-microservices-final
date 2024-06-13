package com.bankingapp.account_services.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @SequenceGenerator(name = "user_id_sequence",sequenceName =  "user_id_sequence",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator =  "user_id_sequence")
    private Long id;
    private String firstname;
    private String lastname;
    @Column(unique = true)
    private String email;
    private boolean isEmailVerified;
    private String password;
    private String postCode;
    private String streetName;
    private String role;
    private String verificationToken;
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<BankAccount> bankAccounts = new ArrayList<>();
}
