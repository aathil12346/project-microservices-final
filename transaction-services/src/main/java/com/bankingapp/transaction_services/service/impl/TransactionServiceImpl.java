package com.bankingapp.transaction_services.service.impl;

import com.bankingapp.transaction_services.dto.AmountTransferRequestDto;
import com.bankingapp.transaction_services.dto.BankResponseDto;
import com.bankingapp.transaction_services.entity.Transaction;
import com.bankingapp.transaction_services.repository.TransactionRepository;
import com.bankingapp.transaction_services.security.JwtUtils;
import com.bankingapp.transaction_services.service.ApiClient;
import com.bankingapp.transaction_services.service.TransactionService;
import com.bankingapp.transaction_services.utils.TransactionUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private ApiClient apiClient;
    @Autowired
    private JwtUtils utils;

    /**
     * method which allows the user to transfer money between two bank accounts
     * @param requestDto transfer request details
     * @return bank response
     */
    @Override
    public BankResponseDto transfer(AmountTransferRequestDto requestDto) {

        if (requestDto.getSenderAccountNumber().equals(requestDto.getRecipientAccountNumber())){
            return BankResponseDto.builder()
                    .message(TransactionUtils.TRANSACTION_FAILED_MSG)
                    .statusCode(TransactionUtils.TRANSACTION_FAILED_CODE).build();
        }

        boolean doesSenderAccountExists = apiClient.findAccountExists(requestDto.getSenderAccountNumber());

        if (!doesSenderAccountExists){
            return BankResponseDto.builder()
                    .message(TransactionUtils.SENDER_ACCOUNT_DOES_NOT_EXISTS_MSG)
                    .statusCode(TransactionUtils.SENDER_ACCOUNT_DOES_NOT_EXISTS_CODE).build();
        }

        boolean doesReceiverAccountExists = apiClient.findAccountExists(requestDto.getRecipientAccountNumber());
        if (!doesReceiverAccountExists){
            return BankResponseDto.builder()
                    .message(TransactionUtils.RECEIVER_ACCOUNT_DOES_NOT_EXISTS_MSG)
                    .statusCode(TransactionUtils.RECEIVER_ACCOUNT_DOES_NOT_EXISTS_CODE).build();
        }


        HttpStatus debitStatus = apiClient.debitFromAnAccount(requestDto);

        if (debitStatus.equals(HttpStatus.EXPECTATION_FAILED)){
            return BankResponseDto.builder()
                    .message(TransactionUtils.TRANSACTION_FAILED_MSG)
                    .statusCode(TransactionUtils.TRANSACTION_FAILED_CODE).build();
        }
        HttpStatus creditStatus = apiClient.creditToAnAccount(requestDto);

        if (creditStatus.equals(HttpStatus.EXPECTATION_FAILED)){
            return BankResponseDto.builder()
                    .message(TransactionUtils.TRANSACTION_FAILED_MSG)
                    .statusCode(TransactionUtils.TRANSACTION_FAILED_CODE).build();
        }



        Transaction transaction = Transaction.builder()
                .amount(requestDto.getAmountToBeTransferred())
                .senderAccountNumber(requestDto.getSenderAccountNumber())
                .receiverAccountNumber(requestDto.getRecipientAccountNumber())
                .status("PROCESSED")
                .timeOfTransaction(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        return BankResponseDto.builder()
                .message(TransactionUtils.TRANSACTION_SUCCESS_MSG)
                .statusCode(TransactionUtils.TRANSACTION_SUCCESS_CODE).build();

    }

    /**
     * method which allows the user to view all transactions from or to a bank account
     * @param accountNumber
     * @return
     */
    @Override
    public List<Transaction> viewTransactions(String accountNumber) {
        return transactionRepository.findTransactionBySenderAccountNumberOrReceiverAccountNumber(accountNumber,accountNumber);
    }

    /**
     * method which allows the user to download bank statements
     * @param transactions list of transactions
     * @return byte array
     * @throws DocumentException
     */
    @Override
    public byte[] getBankStatement(List<Transaction> transactions) throws DocumentException {

        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, baos);
        document.open();


        document.add(new Paragraph("Bank Statement"));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);

        // Add table headers
        table.addCell("Transaction ID");
        table.addCell("Sender");
        table.addCell("Amount");
        table.addCell("Receiver");
        table.addCell("Status");
        table.addCell("Date");


        // some pieces of the following code is taken from: https://www.tutorialspoint.com/itext/itext_creating_pdf_document.htm#:~:text=%2F%2F%20Creating%20a%20PdfDocument%20PdfDocument,methods%20provided%20by%20its%20class.
        for (Transaction transaction : transactions) {
            table.addCell(transaction.getTransactionId());
            table.addCell(transaction.getSenderAccountNumber());
            table.addCell(transaction.getAmount().toString());
            table.addCell(transaction.getReceiverAccountNumber());
            table.addCell(transaction.getStatus());
            table.addCell(transaction.getTimeOfTransaction().toString());
        }


        document.add(table);

        document.close();
        return baos.toByteArray();
    }

    /**
     * method which retrieves all transaction made within a specified month
     * @param accountNumber account number
     * @param year year
     * @param month month
     * @return list of transactions
     */
    @Override
    public List<Transaction> getTransactionsByMonth(String accountNumber, int year, int month) {

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        return transactionRepository.findByAccountNumberAndMonth(accountNumber,startDate,endDate);
    }

}

