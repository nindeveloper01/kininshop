package com.phoneshop.controller;

import com.phoneshop.dto.ColorDTO;
import com.phoneshop.entity.Color;
import com.phoneshop.service.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/colors")
public class ColorController {
    private final ColorService colorService;
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ColorDTO color) {
        ColorDTO colorDTO = colorService.create(color);
        return ResponseEntity.ok(colorDTO);
    }
}