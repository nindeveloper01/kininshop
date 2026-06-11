package com.phoneshop.repository;

import com.phoneshop.entity.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileUploadRepository extends JpaRepository<FileUpload, UUID> {
    FileUpload findByFileName(String fileName);
}