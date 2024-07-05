package com.bankingapp.transaction_services.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TransactionUtils {

    public static final String SENDER_ACCOUNT_DOES_NOT_EXISTS_CODE = "E-404";
    public static final String RECEIVER_ACCOUNT_DOES_NOT_EXISTS_CODE = "E-405";

    public static final String SENDER_ACCOUNT_DOES_NOT_EXISTS_MSG = "Sender account does not exists";
    public static final String RECEIVER_ACCOUNT_DOES_NOT_EXISTS_MSG = "Receiver account does not exists";

    public static final String TRANSACTION_FAILED_CODE = "ET-001";
    public static final String TRANSACTION_FAILED_MSG = "Transaction failed due to insufficient balance";

    public static final String TRANSACTION_SUCCESS_CODE = "ST-001";
    public static final String TRANSACTION_SUCCESS_MSG = "Transaction Processed";

}
