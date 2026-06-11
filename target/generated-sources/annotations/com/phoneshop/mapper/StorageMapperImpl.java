package com.phoneshop.mapper;

import com.phoneshop.dto.StorageDTO;
import com.phoneshop.entity.Storage;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-11T21:38:08+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class StorageMapperImpl implements StorageMapper {

    @Override
    public Storage toStorage(StorageDTO storageDTO) {
        if ( storageDTO == null ) {
            return null;
        }

        Storage storage = new Storage();

        storage.setStorageCapacity( storageDTO.getStorageCapacity() );

        return storage;
    }

    @Override
    public StorageDTO toStorageDTO(Storage storage) {
        if ( storage == null ) {
            return null;
        }

        StorageDTO storageDTO = new StorageDTO();

        storageDTO.setStorageCapacity( storage.getStorageCapacity() );

        return storageDTO;
    }
}
