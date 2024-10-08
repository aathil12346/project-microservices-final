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
import org.springframework.http.HttpStatus;
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

    /**
     * method which allows the user to open a new bank account
     * @param requestDto user request details
     * @param file government id of the user for KYC
     * @return a bank response
     */
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
                accountStatus("PENDING VERIFICATION")
                .accountType(requestDto.getAccountType()).build();

        if (bankAccount.getAccountType().name().equals("CHECKING")){

            bankAccount.setInterestRate(1);
        }else {

            bankAccount.setInterestRate(8);
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

    /**
     * Method which sends a verification token to a user email
     * @param user the recipient user
     */
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

    /**
     * method which verifies the token send via email
     * @param token token presented by the user
     * @return String which indicates whether the token is valid or not
     */
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

    /**
     * method to allow a user to login using username and password
     * @param loginInformationDto contains username and password information of the user
     * @return a valid JWT
     */
    @Override
    public String login(LoginInformationDto loginInformationDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginInformationDto.getEmail(),loginInformationDto.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtUtils.generateJwt(authentication);

    }

    /**
     * method which allows a user to view their user details
     * @param request HTTP request
     * @return user details
     */
    @Override
    public UserInfoDto getUserDetails(HttpServletRequest request) {

        User user = getUser(request);

        return UserInfoDto.builder()
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .postCode(user.getPostCode())
                .streetName(user.getStreetName())
                .isEmailVerified(user.isEmailVerified())
                .build();

    }

    /**
     * method which allows the user to retrieve all their bank account details
     * @param request HTTP request
     * @return List of bank accounts associated with the user
     */
    @Override
    public List<BankAccountInfoDto> getAccountDetails(HttpServletRequest request) {

        User user = getUser(request);
        return user.getBankAccounts().stream().map(this::entityToDto).toList();


    }

    /**
     * method which allows the already registered user to open additional bank accounts
     * @param request HTTP request
     * @param accountType String account type (SAVINGS or CHECKING)
     * @return Bank response
     */
    @Override
    public BankResponseDto addBankAccount(HttpServletRequest request, String accountType) {
        User user = getUser(request);

        BankAccount bankAccount = BankAccount.builder()
                .accountType(AccountType.valueOf(accountType))
                .accountStatus("PENDING VERIFICATION")
                .accountBalance(BigDecimal.ZERO)
                .accountNumber(BankAccountUtils.generateBankAccountNumber())
                .build();

        if (bankAccount.getAccountType().name().equals("CHECKING")){

            bankAccount.setInterestRate(1);
        }else if (bankAccount.getAccountType().name().equals("SAVINGS")) {
            bankAccount.setInterestRate(8);
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

    /**
     * method which extracts the username from the JWT in the request header
     * @param request HTTP request
     * @return user entity is returned
     */
    private User getUser(HttpServletRequest request) {
        String token = jwtUtils.extractJwtFromRequest(request);

        String username = jwtUtils.getUsername(token);

        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * method which allows the user to change their current address
     * @param requestDto request details containing new address information
     * @param governmentId valid governmentId as address proof
     * @param request Http request
     * @return bank response
     */
    @Override
    public BankResponseDto changeAddress(AddressChangeRequestDto requestDto, MultipartFile governmentId, HttpServletRequest request) {

        User user = getUser(request);

        try {
            fileUploadService.uploadFile(governmentId,user.getEmail()+" address-change");
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

    /**
     * method which checks if a bank account exists or not
     * @param accountNumber bank account number
     * @return boolean indiating whether a bank account exists or not
     */
    @Override
    public boolean doesAccountExists(String accountNumber) {
        return bankAccountRepository.existsBankAccountsByAccountNumber(accountNumber);
    }

    /**
     * method which debits money from an account
     * @param requestDto debit request details
     * @return Http status
     */
    @Override
    public HttpStatus debitFromAnAccount(AmountTransferRequestDto requestDto) {
        BankAccount senderAccount = bankAccountRepository.findBankAccountByAccountNumber(requestDto.getSenderAccountNumber());
        if (senderAccount.getAccountBalance().compareTo(requestDto.getAmountToBeTransferred()) < 0){
            return HttpStatus.EXPECTATION_FAILED;
        }

        senderAccount.setAccountBalance(senderAccount.getAccountBalance().subtract(requestDto.getAmountToBeTransferred()));
        bankAccountRepository.save(senderAccount);
        EmailRequestDto emailRequestDto = EmailRequestDto.builder()
                .subject("Amount Debited")
                .message("An Amount of : " + requestDto.getAmountToBeTransferred() + " " + "was debited to account number : " + requestDto.getRecipientAccountNumber())
                .recipient(senderAccount.getUser().getEmail()).build();
        apiClient.sendEmail(emailRequestDto);
        return HttpStatus.OK;

    }

    /**
     * method which credits money to a bank account
     * @param requestDto credit request details
     * @return Http status
     */
    @Override
    public HttpStatus creditToAnAccount(AmountTransferRequestDto requestDto) {
        BankAccount recipientAccount = bankAccountRepository.findBankAccountByAccountNumber(requestDto.getRecipientAccountNumber());
        BankAccount senderAccount = bankAccountRepository.findBankAccountByAccountNumber(requestDto.getSenderAccountNumber());

        recipientAccount.setAccountBalance(recipientAccount.getAccountBalance().add(requestDto.getAmountToBeTransferred()));
        bankAccountRepository.save(recipientAccount);

        EmailRequestDto emailRequestDto = EmailRequestDto.builder()
                .subject("Amount Credited")
                .message("An Amount of : " + requestDto.getAmountToBeTransferred() + " " + "was credited to account number : " + requestDto.getRecipientAccountNumber()
                + " " + "from account number : " + senderAccount.getAccountNumber())
                .recipient(recipientAccount.getUser().getEmail()).build();
        apiClient.sendEmail(emailRequestDto);
        return HttpStatus.OK;
    }

    /**
     * method which returns a user details hiding sensitive information
     * @param email user email
     * @return user details
     */
    @Override
    public UserSecurityInfo getUser(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()){
            throw new UsernameNotFoundException("Username not found");
        }
        UserSecurityInfo info = UserSecurityInfo.builder()
                .email(user.get().getEmail())
                .password(user.get().getPassword())
                .role(user.get().getRole()).build();
        return info;
    }

    /**
     * method which converts a bank account entity to its dto class
     * @param account bank account entity
     * @return bank account dto
     */
    private BankAccountInfoDto entityToDto(BankAccount account){

        return BankAccountInfoDto.builder()
                .accountBalance(account.getAccountBalance())
                .accountNumber(account.getAccountNumber())
                .accountStatus(account.getAccountStatus())
                .accountType(account.getAccountType())
                .interestRate(account.getInterestRate())
                .createdAt(account.getCreatedAt())
                .modifiedAt(account.getModifiedAt())
                .build();


    }

}
