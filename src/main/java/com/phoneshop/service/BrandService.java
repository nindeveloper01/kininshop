package com.phoneshop.service;

import com.phoneshop.dto.*;
import com.phoneshop.entity.Brand;
import com.phoneshop.exception.*;
import com.phoneshop.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {

    private final BrandRepository brandRepository;

    public List<BrandResponse> findAll() {
        return brandRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public BrandResponse findById(Long id) {
        return toResponse(getBrandOrThrow(id));
    }

    @Transactional
    public BrandResponse create(BrandRequest request) {
        if (brandRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Brand already exists: " + request.getName());
        }
        Brand brand = Brand.builder()
                .name(request.getName())
                .country(request.getCountry())
                .build();
        return toResponse(brandRepository.save(brand));
    }

    @Transactional
    public BrandResponse update(Long id, BrandRequest request) {
        Brand brand = getBrandOrThrow(id);
        brand.setName(request.getName());
        brand.setCountry(request.getCountry());
        return toResponse(brandRepository.save(brand));
    }

    @Transactional
    public void delete(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new ResourceNotFoundException("Brand", id);
        }
        brandRepository.deleteById(id);
    }

    // Package-visible helper used by PhoneService
    Brand getBrandOrThrow(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", id));
    }

    private BrandResponse toResponse(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .country(brand.getCountry())
                .build();
    }
}
