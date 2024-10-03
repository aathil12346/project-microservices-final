package com.bankingapp.account_services.service.impl;

import com.bankingapp.account_services.dto.BankResponseDto;
import com.bankingapp.account_services.dto.LoanDetailsDto;
import com.bankingapp.account_services.dto.LoanRequestDto;
import com.bankingapp.account_services.entity.Loan;
import com.bankingapp.account_services.repository.LoanRepository;
import com.bankingapp.account_services.security.JwtUtils;
import com.bankingapp.account_services.service.LoanService;
import com.bankingapp.account_services.utils.BankAccountUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoanServiceImpl implements LoanService {
    @Autowired
    private JwtUtils utils;
    @Autowired
    private S3FileUploadServiceImpl fileUploadService;
    @Autowired
    private LoanRepository loanRepository;

    /**
     * method which allows a user to apply for an unsecured loan
     * @param request Http request
     * @param requestDto loan request details
     * @return Bank response
     */
    @Override
    public BankResponseDto applyForUnSecuredLoan(HttpServletRequest request, LoanRequestDto requestDto) {

        String token = utils.extractJwtFromRequest(request);

        String user = utils.getUsername(token);

        Loan loan = Loan.builder()
                .loanAmount(requestDto.getLoanAmount())
                .interestRate(requestDto.getPreferredInterestRate())
                .termInMonths(requestDto.getPreferredTermsInMonths())
                .loanStatus("PROCESSING")
                .loanType("UNSECURED")
                .applicationTime(LocalDateTime.now())
                .user(user).build();

        loanRepository.save(loan);

        return BankResponseDto.builder()
                .message(BankAccountUtils.LOAN_REQUEST_SUCCESS_MSG)
                .statusCode(BankAccountUtils.LOAN_REQUEST_SUCCESS_CODE).build();
    }

    /**
     * method which allows the user to apply for a secured loan
     * @param request Http request
     * @param requestDto loan request details
     * @param file Collateral document
     * @return bank response
     */
    @Override
    public BankResponseDto applyForSecuredLoan(HttpServletRequest request, LoanRequestDto requestDto, MultipartFile file) {

        String token = utils.extractJwtFromRequest(request);

        String username = utils.getUsername(token);

        try {
            fileUploadService.uploadFile(file,"Collateral" + " " + username);
        }catch (IOException exception){
            return BankResponseDto.builder()
                    .message(exception.getMessage())
                    .statusCode(BankAccountUtils.UNABLE_TO_UPLOAD_FILE_CODE).build();
        }

        Loan loan = Loan.builder()
                .loanType("SECURED")
                .loanAmount(requestDto.getLoanAmount())
                .interestRate(requestDto.getPreferredInterestRate())
                .termInMonths(requestDto.getPreferredTermsInMonths())
                .loanStatus("PROCESSING")
                .user(username)
                .applicationTime(LocalDateTime.now()).build();

        loanRepository.save(loan);

        return BankResponseDto.builder()
                .message(BankAccountUtils.LOAN_REQUEST_SUCCESS_MSG)
                .statusCode(BankAccountUtils.LOAN_REQUEST_SUCCESS_CODE).build();
    }

    /**
     * method which allows the user to retrieve all their loan application details
     * @param request Http request
     * @return list of loans
     */
    @Override
    public List<LoanDetailsDto> getLoanDetails(HttpServletRequest request) {

        String token = utils.extractJwtFromRequest(request);

        String username = utils.getUsername(token);

        List<Loan> loan = loanRepository.findLoanByUser(username);

        return loan.stream().map(this::convertLoantoLoanDetailsDto).toList();



    }

    /**
     * method which converts a loan entity to loan dto class
     * @param loan loan entity
     * @return loan dto
     */
    private LoanDetailsDto convertLoantoLoanDetailsDto(Loan loan){

        return  LoanDetailsDto.builder()
                .id(loan.getId())
                .loanAmount(loan.getLoanAmount())
                .loanType(loan.getLoanType())
                .loanStatus(loan.getLoanStatus())
                .preferredTermInMonths(loan.getTermInMonths())
                .preferredInterestRate(loan.getInterestRate())
                .applicationTime(loan.getApplicationTime())
                .build();

    }

    /**
     * method which allows the user to cancel a loan application
     * @param loanId loan id
     * @return bank response
     */
    @Transactional
    @Override
    public BankResponseDto cancelLoan(Long loanId) {

        Optional<Loan> loan = loanRepository.findById(loanId);

        if (loan.isEmpty()){

            return BankResponseDto.builder()
                    .statusCode(BankAccountUtils.LOAN_NOT_FOUND_CODE)
                    .message(BankAccountUtils.LOAN_NOT_FOUND_MSG).build();
        }

        loanRepository.delete(loan.get());

        return BankResponseDto.builder()
                .message(BankAccountUtils.LOAN_CANCELLATION_REQUEST_MSG)
                .statusCode(BankAccountUtils.LOAN_CANCELLATION_REQUEST_CODE).build();

    }

}
