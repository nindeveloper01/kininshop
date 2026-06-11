package com.phoneshop.controller;

import com.phoneshop.dto.PriceDTO;
import com.phoneshop.dto.ProductDTO;
import com.phoneshop.dto.ProductImportDTO;
import com.phoneshop.entity.Product;
import com.phoneshop.mapper.ProductMapper;
import com.phoneshop.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductMapper productMapper;
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ProductDTO productDTO ) {
        Product product = productMapper.toProduct(productDTO);
        product = productService.create(product);
        return ResponseEntity.ok(product);
    }

    @PostMapping("/importProduct")
    public ResponseEntity<?> importProduct(@RequestBody @Valid ProductImportDTO importDTO){
        productService.importProduct(importDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/uploadProduct")
    public ResponseEntity<?> uploadProduct(@RequestParam("file") MultipartFile file){
        Map<Integer, String> errorMap = productService.uploadProduct(file);
        return ResponseEntity.ok(errorMap);
    }
    @PostMapping("/{productId}/setSalePrice")
    public ResponseEntity<?> setSalePrice(@PathVariable Long productId, @RequestBody PriceDTO priceDTO){
        productService.setSalePrice(productId, priceDTO);
        return ResponseEntity.ok().build();
    }
}
