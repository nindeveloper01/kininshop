package com.phoneshop.controller;

import com.phoneshop.dto.*;
import com.phoneshop.service.PhoneService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/phones")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")

public class PhoneController {

    private final PhoneService phoneService;

    /**
     * GET /api/v1/phones?page=0&size=10&keyword=samsung
     * Public endpoint — no auth required.
     */
    @GetMapping
    public ResponseEntity<PageResponse<PhoneResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(phoneService.findAll(page, size, keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhoneResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(phoneService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhoneResponse> create(@Valid @RequestBody PhoneRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(phoneService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhoneResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody PhoneRequest request) {
        return ResponseEntity.ok(phoneService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        phoneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
