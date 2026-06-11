package com.phoneshop.mapper;

import com.phoneshop.dto.BrandDTO;
import com.phoneshop.entity.Brand;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-11T16:37:11+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class BrandMapperImpl implements BrandMapper {

    @Override
    public Brand toBrand(BrandDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Brand.BrandBuilder brand = Brand.builder();

        brand.name( dto.getName() );

        return brand.build();
    }

    @Override
    public BrandDTO toBrandDTO(Brand entity) {
        if ( entity == null ) {
            return null;
        }

        BrandDTO brandDTO = new BrandDTO();

        brandDTO.setName( entity.getName() );

        return brandDTO;
    }
}
