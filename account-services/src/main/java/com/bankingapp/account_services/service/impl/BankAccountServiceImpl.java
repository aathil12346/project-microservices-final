package com.bankingapp.account_services.service.impl;

import com.bankingapp.account_services.config.SecurityConfig;
import com.bankingapp.account_services.dto.*;
import com.bankingapp.account_services.entity.AccountType;
import com.bankingapp.account_services.entity.BankAccount;
import com.bankingapp.account_services.entity.User;
import com.bankingapp.account_services.entity.VerificationToken;
import com.bankingapp.account_services.repository.BankAccountRepository;
import com.bankingapp.account_services.repository.UserRepository;
import com.bankingapp.account_services.repository.VerificationTokenRepository;
import com.bankingapp.account_services.security.JwtUtils;
import com.bankingapp.account_services.service.ApiClient;
import com.bankingapp.account_services.service.BankAccountService;
import com.bankingapp.account_services.service.S3FileUploadService;
import com.bankingapp.account_services.utils.BankAccountUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;

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

    @Override
    public String login(LoginInformationDto loginInformationDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginInformationDto.getEmail(),loginInformationDto.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtUtils.generateJwt(authentication);

    }

    @Override
    public UserInfoDto getUserDetails(HttpServletRequest request) {

        String token = jwtUtils.extractJwtFromRequest(request);

        String username = jwtUtils.getUsername(token);

        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return UserInfoDto.builder()
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .postCode(user.getPostCode())
                .streetName(user.getStreetName())
                .isEmailVerified(user.isEmailVerified())
                .build();

    }

    @Override
    public List<BankAccountInfoDto> getAccountDetails(HttpServletRequest request) {

        String token = jwtUtils.extractJwtFromRequest(request);

        String username = jwtUtils.getUsername(token);

        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return user.getBankAccounts().stream().map(this::entityToDto).toList();


    }

    @Override
    public BankResponseDto addBankAccount(HttpServletRequest request, String accountType) {
        String token = jwtUtils.extractJwtFromRequest(request);

        String username = jwtUtils.getUsername(token);

        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        BankAccount bankAccount = BankAccount.builder()
                .accountType(AccountType.valueOf(accountType))
                .accountStatus("PENDING VERIFICATION")
                .accountBalance(BigDecimal.ZERO)
                .accountNumber(BankAccountUtils.generateBankAccountNumber())
                .build();

        if (bankAccount.getAccountType().name().equals("CHECKING")){

            bankAccount.setInterestRate(1);
            bankAccount.setOverdraftLimit(5000);
        }else if (bankAccount.getAccountType().name().equals("SAVINGS")) {
            bankAccount.setInterestRate(8);
            bankAccount.setOverdraftLimit(0);
        }else {
            throw new RuntimeException("Account type can either be SAVINGS or CHECKING");
        }

        user.getBankAccounts().add(bankAccount);
        bankAccount.setUser(user);

        bankAccountRepository.save(bankAccount);

        return BankResponseDto.builder()
                .statusCode(BankAccountUtils.ADD_BANK_ACCOUNT_REQUEST_RAISED_CODE)
                .message(BankAccountUtils.ADD_BANK_ACCOUNT_REQUEST_RAISED_CODE_MSG)
                .build();


    }

    @Override
    public BankResponseDto changeAddress(AddressChangeRequestDto requestDto, MultipartFile govermentId, HttpServletRequest request) {

        String token = jwtUtils.extractJwtFromRequest(request);

        String username = jwtUtils.getUsername(token);

        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        try {
            fileUploadService.uploadFile(govermentId,user.getEmail()+" address-change");
        }catch (IOException exception){
            return BankResponseDto.builder()
                    .message(exception.getMessage())
                    .statusCode(BankAccountUtils.UNABLE_TO_UPLOAD_FILE_CODE).build();
        }

        user.setPostCode(requestDto.getPostcode());
        user.setStreetName(requestDto.getStreetName());

        userRepository.save(user);

        return BankResponseDto.builder()
                .statusCode(BankAccountUtils.ADDRESS_CHANGE_REQUEST_SUCCESSFULL_CODE)
                .message(BankAccountUtils.ADDRESS_CHANGE_REQUEST_SUCCESSFULL_MSG).build();


    }

    private BankAccountInfoDto entityToDto(BankAccount account){

        return BankAccountInfoDto.builder()
                .accountBalance(account.getAccountBalance())
                .accountNumber(account.getAccountNumber())
                .accountStatus(account.getAccountStatus())
                .accountType(account.getAccountType())
                .interestRate(account.getInterestRate())
                .createdAt(account.getCreatedAt())
                .modifiedAt(account.getModifiedAt())
                .overdraftLimit(account.getOverdraftLimit()).build();


    }

}
