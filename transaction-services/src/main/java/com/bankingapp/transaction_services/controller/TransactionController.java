package com.bankingapp.transaction_services.controller;

import com.bankingapp.transaction_services.dto.AmountTransferRequestDto;
import com.bankingapp.transaction_services.dto.BankResponseDto;
import com.bankingapp.transaction_services.entity.Transaction;
import com.bankingapp.transaction_services.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("v1/transaction-service")
public class TransactionController {
    @Autowired
     private TransactionService transactionService;

    @PostMapping("/transfer")
    public BankResponseDto transfer(@RequestBody AmountTransferRequestDto requestDto){
        return transactionService.transfer(requestDto);
    }

    @PostMapping("/view-transactions")
    public List<Transaction> viewTransactions(@RequestParam("accountNumber")String accountNumber){

        return transactionService.viewTransactions(accountNumber);
    }

    @GetMapping("/bank-statement")
    public ResponseEntity<ByteArrayResource> getBankStatement(@RequestParam("accountNumber") String accountNumber,
                                                              @RequestParam("year") int year,
                                                              @RequestParam("month")int month,
                                                              HttpServletRequest request) throws IOException {

        try {
            List<Transaction> transactions = transactionService.getTransactionsByMonth(accountNumber, year, month);

            byte[] pdfContent = transactionService.getBankStatement(transactions);


            ByteArrayResource resource = new ByteArrayResource(pdfContent);


            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=bank_statement.pdf")
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .contentLength(pdfContent.length)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
