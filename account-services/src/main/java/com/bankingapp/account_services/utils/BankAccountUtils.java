package com.bankingapp.account_services.utils;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class BankAccountUtils {

    public static final String ACCOUNT_CREATION_REQUEST_RAISED_CODE = "001";
    public static final String ACCOUNT_CREATION_REQUEST_RAISED_MSG = "Bank account creation request raised successfully" + "\n"
            +"Please Verify your email using the link sent to your mail" + "\n" +
            "Upload a government Id to complete all verification process";

    public static String generateBankAccountNumber() {
        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;
        return "AATH" + randomNumber;
    }
}
