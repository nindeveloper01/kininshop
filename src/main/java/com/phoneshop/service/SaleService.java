package com.phoneshop.service;

import com.phoneshop.dto.SaleDTO;
import com.phoneshop.entity.Sale;

public interface SaleService {
    void sell(SaleDTO saleDTO);
    Sale getById(Long saleId);
    void cancelSale(Long saleId);
}
