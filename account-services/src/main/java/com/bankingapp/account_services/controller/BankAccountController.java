package com.bankingapp.account_services.controller;

import com.bankingapp.account_services.dto.BankResponseDto;
import com.bankingapp.account_services.dto.CreateBankAccountRequestDto;
import com.bankingapp.account_services.entity.VerificationToken;
import com.bankingapp.account_services.repository.VerificationTokenRepository;
import com.bankingapp.account_services.service.BankAccountService;
import com.bankingapp.account_services.service.S3FileUploadService;
import com.bankingapp.account_services.utils.BankAccountUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("v1/account-services")
public class BankAccountController {

    @Autowired
    private BankAccountService bankAccountService;



    @PostMapping(value = "/create-account",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BankResponseDto> createBankAccount(@Valid @RequestPart(value = "requestDto") CreateBankAccountRequestDto requestDto,
                                             @RequestPart(value = "file") MultipartFile governmentId) throws IOException {

        if (governmentId == null || governmentId.isEmpty()) {
            return new ResponseEntity<>(BankResponseDto.builder()
                    .statusCode(BankAccountUtils.FILE_FIELD_EMPTY_CODE)
                    .message(BankAccountUtils.FILE_FIELD_EMPTY_MSG).build(),HttpStatus.BAD_REQUEST);
        }

        BankResponseDto bankResponseDto = bankAccountService.createBankAccount(requestDto,governmentId);
        if (!bankResponseDto.getStatusCode().equals("S-001")){

            return new ResponseEntity<>(bankResponseDto,HttpStatus.EXPECTATION_FAILED);
        }
        return new ResponseEntity<>(bankResponseDto,HttpStatus.OK);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyToken(@RequestParam(value = "token")
                                                  String token){

          return new ResponseEntity<>(bankAccountService.verifyToken(token),HttpStatus.OK);
    }


}
