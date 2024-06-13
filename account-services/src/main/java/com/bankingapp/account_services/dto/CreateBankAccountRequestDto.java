package com.bankingapp.account_services.dto;

import com.bankingapp.account_services.entity.AccountType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
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

    //    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#\\$%\\^&\\*])(?=.{8,})$"
//            ,message = "Password should contain atleast one uppercase" + "\n" + "" +
//            "Password should contain atleast one lowercase" + "\n" +
//            "Password should contain atleast one digit" + "\n" + "" +
//            "Password should contain atleast one special character")
    @Size(min = 8 , max = 16 ,message = "Password length should be in the 8 to 16 range" )
    @NotBlank(message = "Please set a password")
    private String password;

    @NotBlank(message = "Postcode cannot be blank")
    @NotEmpty(message = "Postcode cannot be empty")
    private String postcode;
    private String streetName;


    private AccountType accountType;
}
