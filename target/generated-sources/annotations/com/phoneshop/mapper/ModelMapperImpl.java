package com.phoneshop.mapper;

import com.phoneshop.dto.ModelDTO;
import com.phoneshop.entity.Brand;
import com.phoneshop.entity.Model;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-11T16:37:11+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class ModelMapperImpl implements ModelMapper {

    @Override
    public Model toModel(ModelDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Model model = new Model();

        model.setName( dto.getName() );

        return model;
    }

    @Override
    public ModelDTO toModelDTO(Model entity) {
        if ( entity == null ) {
            return null;
        }

        ModelDTO modelDTO = new ModelDTO();

        Long id = entityBrandId( entity );
        if ( id != null ) {
            modelDTO.setBrandId( id.intValue() );
        }
        modelDTO.setName( entity.getName() );

        return modelDTO;
    }

    private Long entityBrandId(Model model) {
        if ( model == null ) {
            return null;
        }
        Brand brand = model.getBrand();
        if ( brand == null ) {
            return null;
        }
        Long id = brand.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
