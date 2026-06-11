package com.phoneshop.mapper;

import com.phoneshop.dto.ModelDTO;
import com.phoneshop.entity.Model;
import com.phoneshop.service.BrandService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring" ,uses = {BrandService.class})
public interface ModelMapper {
//    @Mapping(target = "brand" ,source = "brandId")
    Model toModel(ModelDTO dto);
    @Mapping(target = "brandId", source = "brand.id")
    ModelDTO toModelDTO(Model entity);
}
