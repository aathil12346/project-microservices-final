package com.bankingapp.account_services.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSecurityInfo {

    private String email;
    private String password;
    private String role;
}
