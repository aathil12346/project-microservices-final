package com.bankingapp.account_services.exception;

import com.bankingapp.account_services.dto.BankResponseDto;
import com.bankingapp.account_services.utils.BankAccountUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    /**
     * method which handles all input field validation errors
     * @param ex exception
     * @param headers Http headers
     * @param status Http status
     * @param request Web request
     * @return list of errors
     */
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


    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<BankResponseDto> handleUsernameNotFoundException(UsernameNotFoundException exception,WebRequest request){

        BankResponseDto responseDto = BankResponseDto.builder()
                .message(exception.getMessage())
                .statusCode(BankAccountUtils.USERNAME_NOT_FOUND_CODE).build();

        return new ResponseEntity<>(responseDto,HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e){

        return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
    }
}
