package com.phoneshop.service;

import com.phoneshop.dto.StorageDTO;
import com.phoneshop.entity.Storage;

public interface StorageService {
    StorageDTO create(StorageDTO storageDTO);
    Storage getById(Long id);
}
