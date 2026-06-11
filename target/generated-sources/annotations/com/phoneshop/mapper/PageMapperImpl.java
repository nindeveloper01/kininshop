package com.phoneshop.mapper;

import com.phoneshop.dto.PageDTO;
import com.phoneshop.dto.PaginationDTO;
import javax.annotation.processing.Generated;
import org.springframework.data.domain.Page;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-11T16:37:11+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
public class PageMapperImpl implements PageMapper {

    @Override
    public PageDTO toDTO(Page<?> page) {
        if ( page == null ) {
            return null;
        }

        Page<?> page1 = null;

        page1 = page;

        PageDTO pageDTO = new PageDTO( page1 );

        pageDTO.setPagination( toPaginationDTO(page) );
        pageDTO.setList( page.getContent() );

        return pageDTO;
    }

    @Override
    public PaginationDTO toPaginationDTO(Page<?> page) {
        if ( page == null ) {
            return null;
        }

        PaginationDTO.PaginationDTOBuilder paginationDTO = PaginationDTO.builder();

        paginationDTO.totalPages( page.getTotalPages() );
        paginationDTO.totalElements( page.getTotalElements() );
        paginationDTO.numberOfElements( page.getNumberOfElements() );
        paginationDTO.first( page.isFirst() );
        paginationDTO.last( page.isLast() );
        paginationDTO.empty( page.isEmpty() );

        return paginationDTO.build();
    }
}
