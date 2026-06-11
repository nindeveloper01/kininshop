package com.phoneshop.service.impl;

import com.phoneshop.dto.ColorDTO;
import com.phoneshop.dto.StorageDTO;
import com.phoneshop.entity.Color;
import com.phoneshop.entity.Storage;
import com.phoneshop.mapper.ColorMapper;
import com.phoneshop.mapper.StorageMapper;
import com.phoneshop.repository.ColorRepository;
import com.phoneshop.repository.StorageRepository;
import com.phoneshop.service.ColorService;
import com.phoneshop.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
    private final StorageRepository storageRepository;
    private final StorageMapper storageMapper;
    @Override
    public StorageDTO create(StorageDTO storage) {
        Storage storageEntity = storageMapper.toStorage(storage);
        Storage saved = storageRepository.save(storageEntity);

        return storageMapper.toStorageDTO(saved);
    }

    @Override
    public Storage getById(Long id) {
        return storageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Color not found with id: " + id));
    }
}
