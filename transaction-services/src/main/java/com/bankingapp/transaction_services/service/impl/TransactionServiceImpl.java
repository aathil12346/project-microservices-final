package com.bankingapp.transaction_services.service.impl;

import com.bankingapp.transaction_services.dto.AmountTransferRequestDto;
import com.bankingapp.transaction_services.dto.BankResponseDto;
import com.bankingapp.transaction_services.dto.EmailRequestDto;
import com.bankingapp.transaction_services.entity.Transaction;
import com.bankingapp.transaction_services.repository.TransactionRepository;
import com.bankingapp.transaction_services.security.JwtUtils;
import com.bankingapp.transaction_services.service.ApiClient;
import com.bankingapp.transaction_services.service.TransactionService;
import com.bankingapp.transaction_services.utils.TransactionUtils;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private ApiClient apiClient;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtUtils utils;
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

    @Override
    public List<Transaction> viewTransactions(String accountNumber) {
        return transactionRepository.findTransactionBySenderAccountNumberOrReceiverAccountNumber(accountNumber,accountNumber);
    }

    @Override
    public String getBankStatement(String accountNumber, int month, HttpServletRequest request) throws MessagingException, IOException {

        List<Transaction> transactions = getTransactionsByMonth(accountNumber,month);
        if (transactions.isEmpty()){
            return "No transactions made during this month";
        }

        byte[] bytes = generatePdf(transactions);

        String token = utils.extractJwtFromRequest(request);
        String email = utils.getUsername(token);

        EmailRequestDto requestDto = EmailRequestDto.builder()
                .subject("Bank Statement")
                .recipient(email)
                .message("Bank statement attached with email").build();

        MultipartFile multipartFile = convertByteArrayToMultipartFile(bytes);
        emailService.sendEmailWithAttachment(requestDto,multipartFile);

        return "Success";

    }

    private MultipartFile convertByteArrayToMultipartFile(byte[] pdf){
        return new MultipartFile() {
            @Override
            public String getName() {
                return "BankStatment";
            }

            @Override
            public String getOriginalFilename() {
                return "BankStatment";
            }

            @Override
            public String getContentType() {
                return "application/pdf";
            }

            @Override
            public boolean isEmpty() {
                return pdf == null || pdf.length == 0;
            }

            @Override
            public long getSize() {
                return pdf.length;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return pdf;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(pdf);
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                new FileOutputStream(dest).write(pdf);
            }
        };
    }


    private List<Transaction> getTransactionsByMonth(String accountNumber,int month){

        List<Transaction> transactions = transactionRepository.findTransactionBySenderAccountNumberOrReceiverAccountNumber(accountNumber,accountNumber);

        List<Transaction> filteredTransactions = new ArrayList<>();

        for (Transaction t:transactions) {
            if (t.getTimeOfTransaction().getMonthValue() == month){
                filteredTransactions.add(t);
            }
        }

        return filteredTransactions;
    }

    private byte[] generatePdf(List<Transaction> transactions){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Transaction Report").setTextAlignment(TextAlignment.CENTER).setFontSize(18));

        Table table = new Table(6);
        table.addCell("Transaction ID");
        table.addCell("Sender Account Number");
        table.addCell("Amount");
        table.addCell("Receiver Account Number");
        table.addCell("Status");
        table.addCell("Time of Transaction");

        for (Transaction transaction : transactions) {
            table.addCell(transaction.getTransactionId());
            table.addCell(transaction.getSenderAccountNumber());
            table.addCell(String.valueOf(transaction.getAmount()));
            table.addCell(transaction.getReceiverAccountNumber());
            table.addCell(transaction.getStatus());
            table.addCell(transaction.getTimeOfTransaction().toString());
        }

        document.add(table);
        document.close();
        return baos.toByteArray();
    }
}

