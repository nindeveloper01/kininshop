package com.phoneshop.service.impl;

import com.phoneshop.auditing.AuditContextService;
import com.phoneshop.auditing.CurrentUserService;
import com.phoneshop.entity.FileUpload;
import com.phoneshop.repository.FileUploadRepository;
import com.phoneshop.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {
    private final FileUploadRepository fileUploadRepository;
    private final CurrentUserService currentUserService;
    private final AuditContextService auditContextService;
    private final Path uploadPath = Paths.get("src/main/resources/static/images");

    @Override
    @Transactional
    public String uploadFiles(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new IOException("Original filename is missing");
        }

        String fileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(originalFileName);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Files.copy(file.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    @Override
    @Transactional
    public void insertFile(FileUpload file) {
        auditContextService.setAuditContext(currentUserService.getUserId(), currentUserService.getIpAddress());
        fileUploadRepository.save(file);
    }

    @Override
    public Resource getFile(String fileName) {
        FileUpload fileRecord = fileUploadRepository.findByFileName(fileName);

        if (fileRecord == null) {
            throw new RuntimeException("File not found in DB: " + fileName);
        }

        try {
            // Path to src/main/resources/static/images
            Path imagesPath = Paths.get("src/main/resources/static/images").toAbsolutePath();
            Path filePath = imagesPath.resolve(fileRecord.getFileName()).normalize();

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file: " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error loading file: " + fileName, e);
        }
    }
}
