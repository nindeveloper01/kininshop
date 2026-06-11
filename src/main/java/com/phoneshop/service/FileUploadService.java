package com.phoneshop.service;

import com.phoneshop.entity.FileUpload;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileUploadService {

    String uploadFiles(MultipartFile file) throws IOException;

    void insertFile(FileUpload fileUpload);

    Resource getFile(String fileName);
}
