package com.bankingapp.account_services.dto;

import com.bankingapp.account_services.entity.AccountType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateBankAccountRequestDto {

    @NotEmpty(message = "firstname cannot be empty")
    @NotBlank(message = "firstname cannot be blank")
    private String firstname;

    @NotEmpty(message = "lastname cannot be empty")
    @NotBlank(message = "lastname cannot be blank")
    private String lastname;

    @NotEmpty(message = "Email cannot be empty")
    @NotBlank(message = "Email cannot be blank")
    @Email(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
    private String email;

    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
            message = "Password should have at least one: small letter, one capital letter, one digit, one special character, and be in the range of 8 to 20 characters."
    )
    @NotBlank(message = "Please set a password")
    private String password;

    @NotBlank(message = "Postcode cannot be blank")
    @NotEmpty(message = "Postcode cannot be empty")
    @Size(max = 5)
    private String postcode;
    private String streetName;


    private AccountType accountType;
}
