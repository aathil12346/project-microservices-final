package com.bankingapp.account_services.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3FileUploadService {

    String uploadFile(MultipartFile file,String email) throws IOException;

}
