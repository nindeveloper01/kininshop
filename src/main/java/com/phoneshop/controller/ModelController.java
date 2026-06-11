package com.phoneshop.controller;

import com.phoneshop.dto.ModelDTO;
import com.phoneshop.dto.PageDTO;
import com.phoneshop.exception.ApiException;
import com.phoneshop.mapper.ModelMapper;
import com.phoneshop.service.ModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/models")
@RequiredArgsConstructor
public class ModelController {
    private final ModelService modelService;
    private final ModelMapper modelMapper;

    //    @RolesAllowed("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ModelDTO modelDTO){
        ModelDTO model = modelService.save(modelDTO);
        return ResponseEntity.ok(model);
    }
    @GetMapping("{id}")
    public ResponseEntity<?> getModelById(@PathVariable("id") Long id) throws ApiException {
        ModelDTO model = modelService.getModelById(id);
        return ResponseEntity.ok( model);
    }

    @GetMapping
    public ResponseEntity<?> getModelList(@RequestParam Map<String, String> params){
        PageDTO pageDTO = modelService.getModels(params);
        return ResponseEntity.ok(pageDTO);
    }

}
