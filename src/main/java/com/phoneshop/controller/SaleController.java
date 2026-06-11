package com.phoneshop.controller;

import com.phoneshop.dto.SaleDTO;
import com.phoneshop.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sales")
public class SaleController {
    private final SaleService saleService;
    @PostMapping
    public ResponseEntity<?> create(@RequestBody SaleDTO saleDTO) {
        saleService.sell(saleDTO);
        return ResponseEntity.ok().build();
    }
    @PutMapping("{saleId}/cancel")
    public ResponseEntity<?> cancelSale(@PathVariable Long saleId){
        saleService.cancelSale(saleId);
        return ResponseEntity.ok().build();
    }

}
