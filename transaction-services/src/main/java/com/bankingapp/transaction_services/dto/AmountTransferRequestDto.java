package com.bankingapp.transaction_services.dto;

import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AmountTransferRequestDto {

    private String recipientAccountNumber;
    private String senderAccountNumber;
    private boolean allowOverdraft;
    private BigDecimal amountToBeTransferred;
}
