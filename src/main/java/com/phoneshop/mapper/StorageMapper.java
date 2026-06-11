package com.phoneshop.mapper;

import com.phoneshop.dto.ColorDTO;
import com.phoneshop.dto.StorageDTO;
import com.phoneshop.entity.Color;
import com.phoneshop.entity.Storage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StorageMapper {
    Storage toStorage(StorageDTO storageDTO);
    StorageDTO toStorageDTO(Storage storage);
}
