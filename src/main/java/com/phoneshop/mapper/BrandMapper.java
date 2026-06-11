package com.phoneshop.mapper;

import com.phoneshop.dto.BrandDTO;
import com.phoneshop.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    Brand toBrand(BrandDTO dto);

    BrandDTO toBrandDTO(Brand entity);
}
