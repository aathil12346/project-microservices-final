package com.bankingapp.account_services.service;

import com.bankingapp.account_services.dto.BankResponseDto;
import com.bankingapp.account_services.dto.LoanDetailsDto;
import com.bankingapp.account_services.dto.LoanRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LoanService {

    BankResponseDto applyForUnSecuredLoan(HttpServletRequest request, LoanRequestDto requestDto);

    BankResponseDto applyForSecuredLoan(HttpServletRequest request, LoanRequestDto requestDto, MultipartFile file);

    List<LoanDetailsDto> getLoanDetails(HttpServletRequest request);

    BankResponseDto cancelLoan(Long loanId);
}
