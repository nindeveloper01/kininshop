package com.phoneshop.mapper;

import com.phoneshop.dto.SaleDTO;
import com.phoneshop.entity.Sale;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SaleMapper {
    Sale toSale(SaleDTO saleDTO);
    SaleDTO toSaleDTO(Sale sale);
}
