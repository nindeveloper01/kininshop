package com.phoneshop.mapper;
import com.phoneshop.dto.ProductDTO;
import com.phoneshop.dto.ProductImportDTO;
import com.phoneshop.entity.Product;
import com.phoneshop.entity.ProductImportHistory;
import com.phoneshop.service.ColorService;
import com.phoneshop.service.ModelService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = {ModelService.class, ColorService.class})
public interface ProductMapper {

    @Mapping(target = "model", source = "modelId")
    @Mapping(target = "color", source = "colorId")
    Product toProduct(ProductDTO productDTO);

    @Mapping(target = "dateImport", source = "importDTO.importDate")
    @Mapping(target = "pricePerUnit", source = "importDTO.importPrice")
    @Mapping(target = "product", source = "product")
    @Mapping(target = "id", ignore = true)
    ProductImportHistory toProductImportHistory(ProductImportDTO importDTO, Product product);


}