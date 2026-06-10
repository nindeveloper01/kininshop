package com.phoneshop.service;

import com.phoneshop.dto.*;
import com.phoneshop.entity.*;
import com.phoneshop.exception.ResourceNotFoundException;
import com.phoneshop.repository.PhoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhoneService {

    private final PhoneRepository phoneRepository;
    private final BrandService brandService;

    public PageResponse<PhoneResponse> findAll(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Phone> result = (keyword != null && !keyword.isBlank())
                ? phoneRepository.searchByKeyword(keyword.trim(), pageable)
                : phoneRepository.findAll(pageable);

        return toPageResponse(result);
    }

    public PhoneResponse findById(Long id) {
        return toResponse(getPhoneOrThrow(id));
    }

    @Transactional
    public PhoneResponse create(PhoneRequest request) {
        Brand brand = brandService.getBrandOrThrow(request.getBrandId());
        Phone phone = Phone.builder()
                .model(request.getModel())
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .brand(brand)
                .build();
        return toResponse(phoneRepository.save(phone));
    }

    @Transactional
    public PhoneResponse update(Long id, PhoneRequest request) {
        Phone phone = getPhoneOrThrow(id);
        Brand brand = brandService.getBrandOrThrow(request.getBrandId());
        phone.setModel(request.getModel());
        phone.setPrice(request.getPrice());
        phone.setStock(request.getStock());
        phone.setImageUrl(request.getImageUrl());
        phone.setBrand(brand);
        return toResponse(phoneRepository.save(phone));
    }

    @Transactional
    public void delete(Long id) {
        if (!phoneRepository.existsById(id)) {
            throw new ResourceNotFoundException("Phone", id);
        }
        phoneRepository.deleteById(id);
    }

    // Package-visible helper used by OrderService
    Phone getPhoneOrThrow(Long id) {
        return phoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phone", id));
    }

    private PageResponse<PhoneResponse> toPageResponse(Page<Phone> page) {
        return PageResponse.<PhoneResponse>builder()
                .content(page.getContent().stream().map(this::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    private PhoneResponse toResponse(Phone phone) {
        return PhoneResponse.builder()
                .id(phone.getId())
                .model(phone.getModel())
                .price(phone.getPrice())
                .stock(phone.getStock())
                .imageUrl(phone.getImageUrl())
                .brandId(phone.getBrand().getId())
                .brandName(phone.getBrand().getName())
                .brandCountry(phone.getBrand().getCountry())
                .build();
    }
}
