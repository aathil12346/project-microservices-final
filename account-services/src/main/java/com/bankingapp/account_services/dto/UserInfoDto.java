package com.bankingapp.account_services.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDto {

    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private boolean isEmailVerified;
    private String password;
    private String postCode;
    private String streetName;
}
