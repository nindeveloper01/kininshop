package com.phoneshop.mapper;

import com.phoneshop.dto.ColorDTO;
import com.phoneshop.entity.Color;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ColorMapper {
    Color toColor(ColorDTO colorDTO);
    ColorDTO toColorDTO(Color color);
}
