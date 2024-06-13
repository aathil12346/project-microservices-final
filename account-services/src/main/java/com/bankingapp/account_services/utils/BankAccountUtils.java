package com.bankingapp.account_services.utils;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class BankAccountUtils {

    public static final String ACCOUNT_CREATION_REQUEST_RAISED_CODE = "S-001";
    public static final String ACCOUNT_CREATION_REQUEST_RAISED_MSG = "Bank account creation request raised successfully."
            +" Please Verify your email using the link sent to your mail." +
            " Please note that account will be created only after a successfull Id verification." +
            " We will email you soon with confirmation";

    public static final String UNABLE_TO_UPLOAD_FILE_CODE = "E-101";

    public static final String FILE_FIELD_EMPTY_CODE = "E-102";
    public static final String FILE_FIELD_EMPTY_MSG = "Please upload a file";

    public static final String EMAIL_ALREADY_EXISTS_CODE = "E-001";
    public static final String EMAIL_ALREADY_EXISTS_MSG = "Email already exists , if you" +
            "are an existing user please log in to create additional bank accounts";

    public static String generateBankAccountNumber() {
        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;
        return "AATH" + randomNumber;
    }
}
