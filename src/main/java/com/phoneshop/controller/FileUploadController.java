package com.phoneshop.controller;

import com.phoneshop.dto.FileResponse;
import com.phoneshop.entity.FileUpload;
import com.phoneshop.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api/v1/upload")
@RestController
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

//    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @Operation(summary = "Upload files")
//    public ResponseEntity<?> uploadFiles(@RequestParam("files") List<MultipartFile> files) throws IOException {
//        List<String> fileUrls = new ArrayList<>();
//
//        for (MultipartFile file : files) {
//            String fileName = fileUploadService.uploadFiles(file);
//            // Build the API access URL
//            String url = String.format("http://localhost:8080/api/v1/upload/%s", fileName);
//            fileUploadService.insertFile(new FileUpload(url, fileName));
//            fileUrls.add(url);
//        }
//        return ResponseEntity.ok().body(new FileResponse<>(HttpStatus.CREATED.value(), "Created successfully", fileUrls));
//    }
@PostMapping(
        value = "/file",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
)
@Operation(summary = "Upload files")
public ResponseEntity<FileResponse<List<String>>> uploadFiles(
        @RequestParam("files") List<MultipartFile> files,
        HttpServletRequest request
) throws IOException {

    if (files == null || files.isEmpty()) {
        return ResponseEntity.badRequest()
                .body(new FileResponse<>(
                        HttpStatus.BAD_REQUEST.value(),
                        "No files provided",
                        null
                ));
    }

    List<String> fileUrls = new ArrayList<>();

    String baseUrl = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .build()
            .toUriString();

    for (MultipartFile file : files) {

        if (file.isEmpty()) {
            continue;
        }

        String fileName = fileUploadService.uploadFiles(file);

        String url = String.format(
                "%s/api/v1/upload/%s",
                baseUrl,
                fileName
        );

        fileUploadService.insertFile(
                new FileUpload(url, fileName)
        );

        fileUrls.add(url);
    }

    return ResponseEntity.status(HttpStatus.CREATED)
            .body(
                    new FileResponse<>(
                            HttpStatus.CREATED.value(),
                            "Files uploaded successfully",
                            fileUrls
                    )
            );
}

    @GetMapping("/{fileName}")
    @Operation(summary = "Get file.")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
        Resource file = fileUploadService.getFile(fileName);

        // Detect MIME type
        String contentType;
        try {
            contentType = Files.probeContentType(file.getFile().toPath());
        } catch (IOException e) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        // Special case for Excel if OS detection fails
        if (fileName.endsWith(".xlsx")) {
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if (fileName.endsWith(".xls")) {
            contentType = "application/vnd.ms-excel";
        } else if (fileName.endsWith(".pdf")) {
            contentType = "application/pdf";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(file);
    }

}
