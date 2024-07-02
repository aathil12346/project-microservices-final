package com.bankingapp.account_services.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressChangeRequestDto {

    @NotBlank(message = "Postcode cannot be blank")
    @NotEmpty(message = "Postcode cannot be empty")
    @Size(max = 5)
    private String postcode;
    private String streetName;
}
