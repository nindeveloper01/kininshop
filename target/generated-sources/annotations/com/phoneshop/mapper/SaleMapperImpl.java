package com.phoneshop.mapper;

import com.phoneshop.dto.SaleDTO;
import com.phoneshop.entity.Sale;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-11T16:37:11+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class SaleMapperImpl implements SaleMapper {

    @Override
    public Sale toSale(SaleDTO saleDTO) {
        if ( saleDTO == null ) {
            return null;
        }

        Sale sale = new Sale();

        return sale;
    }

    @Override
    public SaleDTO toSaleDTO(Sale sale) {
        if ( sale == null ) {
            return null;
        }

        SaleDTO saleDTO = new SaleDTO();

        return saleDTO;
    }
}
