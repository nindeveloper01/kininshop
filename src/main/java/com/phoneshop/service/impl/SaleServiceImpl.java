package com.phoneshop.service.impl;
import com.phoneshop.dto.ProductSoldDTO;
import com.phoneshop.dto.SaleDTO;
import com.phoneshop.entity.Product;
import com.phoneshop.entity.Sale;
import com.phoneshop.entity.SaleDetail;
import com.phoneshop.exception.ApiException;
import com.phoneshop.exception.ResourceNotFoundException;
import com.phoneshop.mapper.SaleMapper;
import com.phoneshop.repository.ProductRepository;
import com.phoneshop.repository.SaleDetailRepository;
import com.phoneshop.repository.SaleRepository;
import com.phoneshop.service.ProductService;
import com.phoneshop.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {
    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final SaleMapper saleMapper;
    private final SaleDetailRepository saleDetailRepository;
    @Override
    public void sell(SaleDTO saleDTO) {
        List<Long> productIds = saleDTO.getProducts().stream()
                .map(ProductSoldDTO::getProductId)
                .toList();
        // validate product
        productIds.forEach(productService::getById);

        List<Product> products = productRepository.findAllById(productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));


        // validate stock
        saleDTO.getProducts()
                .forEach(ps ->{
                    Product product = productMap.get(ps.getProductId());
                    if(product.getAvailableUnit() < ps.getNumberOfUnit()) {
                        throw new ApiException(HttpStatus.BAD_REQUEST, "Product [%s] is not enough in stock".formatted(product.getName()));
                    }
                });

        // Sale
        Sale sale = new Sale();
        sale.setSoldDate(saleDTO.getSaleDate());
        saleRepository.save(sale);

        // Sale Detail
        saleDTO.getProducts().forEach(ps ->{
            Product product = productMap.get(ps.getProductId());
            SaleDetail saleDetail = new SaleDetail();
            saleDetail.setAmount(product.getSalePrice());
            saleDetail.setProduct(product);
            saleDetail.setSale(sale);
            saleDetail.setUnit(ps.getNumberOfUnit());
            saleDetailRepository.save(saleDetail);

            // cut stock
            Integer availableUnit =  product.getAvailableUnit() - ps.getNumberOfUnit();
            product.setAvailableUnit(availableUnit);
            productRepository.save(product);
        });
    }

    @Override
    public Sale getById(Long saleId) {
        return saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale", saleId));
    }

    @Override
    public void cancelSale(Long saleId) {
        // update sale status
        Sale sale = getById(saleId);
        sale.setActive(false);
        saleRepository.save(sale);

        // update stock
        List<SaleDetail> saleDetails = saleDetailRepository.findBySaleId(saleId);

        List<Long> productIds = saleDetails.stream()
                .map(sd -> sd.getProduct().getId())
                .toList();

        List<Product> products = productRepository.findAllById(productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        saleDetails.forEach(sd ->{
            Product product = productMap.get(sd.getProduct().getId());
            product.setAvailableUnit(product.getAvailableUnit() + sd.getUnit());
            productRepository.save(product);
        });
    }
    private void saveSale(SaleDTO saleDTO) {
        Sale sale = new Sale();
        sale.setSoldDate(saleDTO.getSaleDate());
        saleRepository.save(sale);


        //Sale Detail
        saleDTO.getProducts().forEach(ps ->{
            SaleDetail saleDetail = new SaleDetail();
            saleDetail.setAmount(null);
        });
    }

    private void validate(SaleDTO saleDTO) {
        saleDTO.getProducts().forEach(ps ->{
            Product product = productService.getById(ps.getProductId());
            if(product.getAvailableUnit() < ps.getNumberOfUnit()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Product [%s] is not enough in stock".formatted(product.getName()));
            }
        });
    }



//    private void validate2(SaleDTO saleDTO) {
//
//
//        List<Long> productIds = saleDTO.getProducts().stream()
//                .map(ProductSoldDTO::getProductId)
//                .toList();
//        // validate product
//        productIds.forEach(productService::getById);
//
//        List<Product> products = productRepository.findAllById(productIds);
//        Map<Long, Product> productMap = products.stream()
//                .collect(Collectors.toMap(Product::getId, Function.identity()));
//
//
//        // validate stock
//        saleDTO.getProducts()
//                .forEach(ps ->{
//                    Product product = productMap.get(ps.getProductId());
//                    if(product.getAvailableUnit() < ps.getNumberOfUnit()) {
//                        throw new ApiException(HttpStatus.BAD_REQUEST, "Product [%s] is not enough in stock".formatted(product.getName()));
//                    }
//                });
//
//    }

}
