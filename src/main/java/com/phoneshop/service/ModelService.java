package com.phoneshop.service;


import com.phoneshop.dto.ModelDTO;
import com.phoneshop.dto.PageDTO;
import com.phoneshop.entity.Model;

import java.util.List;
import java.util.Map;

public interface ModelService {
    ModelDTO save(ModelDTO modelDTO);
    List<ModelDTO> getByBrand(Long brandId);
    Model getById(Long id);
    ModelDTO getModelById(Long id);
    PageDTO getModels(Map<String, String> params);
}
