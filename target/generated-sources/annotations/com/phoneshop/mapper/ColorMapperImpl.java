package com.phoneshop.mapper;

import com.phoneshop.dto.ColorDTO;
import com.phoneshop.entity.Color;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-11T16:37:11+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class ColorMapperImpl implements ColorMapper {

    @Override
    public Color toColor(ColorDTO colorDTO) {
        if ( colorDTO == null ) {
            return null;
        }

        Color color = new Color();

        color.setName( colorDTO.getName() );

        return color;
    }

    @Override
    public ColorDTO toColorDTO(Color color) {
        if ( color == null ) {
            return null;
        }

        ColorDTO colorDTO = new ColorDTO();

        colorDTO.setName( color.getName() );

        return colorDTO;
    }
}
