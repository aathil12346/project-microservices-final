package com.bankingapp.account_services.utils;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class BankAccountUtils {

    public static final String ACCOUNT_CREATION_REQUEST_RAISED_CODE = "S-001";
    public static final String ACCOUNT_CREATION_REQUEST_RAISED_MSG = "Bank account creation request raised successfully" + "\n"
            +"Please Verify your email using the link sent to your mail" + "\n" +
            "Upload a government Id to complete all verification process";

    public static final String UNABLE_TO_UPLOAD_FILE_CODE = "E-101";


    public static final String EMAIL_ALREADY_EXISTS_CODE = "E-001";
    public static final String EMAIL_ALREADY_EXISTS_MSG = "Email already exists , if you" +
            "are an existing user please log in to create additional bank accounts";

    public static String generateBankAccountNumber() {
        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;
        return "AATH" + randomNumber;
    }
}
