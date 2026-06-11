package com.phoneshop.service;

import com.phoneshop.dto.PriceDTO;
import com.phoneshop.dto.ProductImportDTO;
import com.phoneshop.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ProductService {
    Product create(Product product);
    Product getById(Long id);

    void importProduct(ProductImportDTO importDTO);

    Map<Integer, String> uploadProduct(MultipartFile file);
    Product getByModelIdAndColorId(Long modelId, Long colorId);

    void setSalePrice(Long productId, PriceDTO priceDTO);
}
