package com.bankingapp.account_services.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bankingapp.account_services.service.S3FileUploadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Service
public class S3FileUploadServiceImpl implements S3FileUploadService {


    @Autowired
    private AmazonS3 s3Client;

    @Value("${app.s3.bucket}")
    private String bucketName;


    @Override
    public String uploadFile(MultipartFile file,String email) throws IOException {
        File fileObj = convertMultiPartFileToFile(file);
        String filename = System.currentTimeMillis() + "_" + email;
        s3Client.putObject(new PutObjectRequest(bucketName,filename,fileObj));
        fileObj.delete();
        return "file uploaded to s3 successfully";
    }




    private File convertMultiPartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }
}
