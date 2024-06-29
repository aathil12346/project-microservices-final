package com.bankingapp.account_services.exception;

import com.bankingapp.account_services.dto.BankResponseDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {

        Map<String,String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors()
                .forEach(error -> {
                    String name = ((FieldError)error).getField();
                    String message = error.getDefaultMessage();
                    errors.put(name,message);
                });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);


    }
    @ExceptionHandler(AppException.class)
    public ResponseEntity<BankResponseDto> handleAppException(AppException exception,WebRequest request){

        BankResponseDto responseDto = BankResponseDto.builder()
                .message(exception.getMessage())
                .statusCode("E-200").build();

        return new ResponseEntity<>(responseDto,HttpStatus.BAD_REQUEST);

    }
}
