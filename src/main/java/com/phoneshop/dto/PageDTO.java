package com.phoneshop.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PageDTO {
    private List<?> list;
    private PaginationDTO pagination;
    public PageDTO(Page<?> page) {
        this.list = page.getContent(); // This will now contain ModelDTOs
        this.pagination = PaginationDTO.builder()
                .pageSize(page.getPageable().getPageSize())
                .pageNumber(page.getPageable().getPageNumber() + 1)
                .totalElements(page.getTotalElements())
                // ... rest of builder
                .build();
    }

}