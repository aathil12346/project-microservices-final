package com.bankingapp.account_services.service.impl;

import com.bankingapp.account_services.config.SecurityConfig;
import com.bankingapp.account_services.dto.BankResponseDto;
import com.bankingapp.account_services.dto.CreateBankAccountRequestDto;
import com.bankingapp.account_services.dto.EmailRequestDto;
import com.bankingapp.account_services.entity.BankAccount;
import com.bankingapp.account_services.entity.User;
import com.bankingapp.account_services.entity.VerificationToken;
import com.bankingapp.account_services.repository.BankAccountRepository;
import com.bankingapp.account_services.repository.UserRepository;
import com.bankingapp.account_services.repository.VerificationTokenRepository;
import com.bankingapp.account_services.service.ApiClient;
import com.bankingapp.account_services.service.BankAccountService;
import com.bankingapp.account_services.service.S3FileUploadService;
import com.bankingapp.account_services.utils.BankAccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class BankAccountServiceImpl implements BankAccountService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private S3FileUploadService fileUploadService;
    @Autowired
    private VerificationTokenRepository tokenRepository;
    @Autowired
    private ApiClient apiClient;

    @Override
    public BankResponseDto createBankAccount(CreateBankAccountRequestDto requestDto
    , MultipartFile file) {

        Optional<User> user = userRepository.findByEmail(requestDto.getEmail());
        if (user.isPresent()){
            return BankResponseDto.builder()
                    .statusCode(BankAccountUtils.EMAIL_ALREADY_EXISTS_CODE)
                    .message(BankAccountUtils.EMAIL_ALREADY_EXISTS_MSG).build();
        }

        try {
            fileUploadService.uploadFile(file,requestDto.getEmail());
        }catch (IOException exception){
            return BankResponseDto.builder()
                    .message(exception.getMessage())
                    .statusCode(BankAccountUtils.UNABLE_TO_UPLOAD_FILE_CODE).build();
        }

        User newUser = new User();
        newUser.setFirstname(requestDto.getFirstname());
        newUser.setLastname(requestDto.getLastname());
        newUser.setEmail(requestDto.getEmail());
        newUser.setPassword(SecurityConfig.passwordEncoder().encode(requestDto.getPassword()));
        newUser.setPostCode(requestDto.getPostcode());
        newUser.setStreetName(requestDto.getStreetName());
        newUser.setRole("ROLE_USER");
        newUser.setEmailVerified(false);

        BankAccount bankAccount = BankAccount.builder().
                accountNumber(BankAccountUtils.generateBankAccountNumber()).
                accountBalance(BigDecimal.ZERO).
                accountStatus("PENDING VERIFICATION").
                accountType(requestDto.getAccountType()).build();

        if (bankAccount.getAccountType().name().equals("CHECKING")){

            bankAccount.setInterestRate(1);
            bankAccount.setOverdraftLimit(5000);
        }else {

            bankAccount.setInterestRate(8);
            bankAccount.setOverdraftLimit(0);
        }

        newUser.getBankAccounts().add(bankAccount);
        bankAccount.setUser(newUser);

        userRepository.save(newUser);
        bankAccountRepository.save(bankAccount);

        sendVerificationToken(newUser);


        return BankResponseDto.builder()
                .statusCode(BankAccountUtils.ACCOUNT_CREATION_REQUEST_RAISED_CODE)
                .message(BankAccountUtils.ACCOUNT_CREATION_REQUEST_RAISED_MSG)
                .build();


    }

    private void sendVerificationToken(User user){

        String token = UUID.randomUUID().toString();
        VerificationToken token1 = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24L)).build();

        tokenRepository.save(token1);
        String verificationUrl = "http://localhost:8081/v1/account-services/verify?token=" + token;
        String message = "Please click the following link to verify your email address:\n" + verificationUrl;
        EmailRequestDto requestDto = EmailRequestDto.builder()
                .recipient(user.getEmail())
                .message(message)
                .subject("Email Verification").build();

        apiClient.sendEmail(requestDto);

    }

    @Override
    public String verifyToken(String token) {

        VerificationToken verificationToken = tokenRepository.findByToken(token);

        if (verificationToken == null) {
            return "Invalid Token";
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "Token Expired, Generate a new token";
        }


        User user = verificationToken.getUser();
        if (user.isEmailVerified()){
            return "User Email already verified";
        }
        user.setEmailVerified(true);
        userRepository.save(user);

        return "Your Email is now Verified";
    }
}
