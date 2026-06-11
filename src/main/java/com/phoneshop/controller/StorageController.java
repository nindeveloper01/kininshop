package com.phoneshop.controller;

import com.phoneshop.dto.StorageDTO;
import com.phoneshop.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/storages")
public class StorageController {
    private final StorageService storageService;
    @PostMapping
    public ResponseEntity<?> create(@RequestBody StorageDTO storage) {
        StorageDTO storageDTO = storageService.create(storage);
        return ResponseEntity.ok(storageDTO);
    }
}