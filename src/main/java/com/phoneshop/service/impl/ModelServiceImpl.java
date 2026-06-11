package com.phoneshop.service.impl;

import com.phoneshop.dto.ModelDTO;
import com.phoneshop.dto.PageDTO;
import com.phoneshop.entity.Model;
import com.phoneshop.exception.ResourceNotFoundException;
import com.phoneshop.mapper.ModelMapper;
import com.phoneshop.repository.ModelRepository;
import com.phoneshop.service.ModelService;
import com.phoneshop.spec.ModelFilter;
import com.phoneshop.spec.ModelSpec;
import org.apache.commons.collections4.MapUtils;

import com.phoneshop.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {
    private final ModelRepository modelRepository;
    private final ModelMapper modelMapper;
    @Override
    public ModelDTO save(ModelDTO modelDTO) {
        Model model = modelMapper.toModel(modelDTO);
        model = modelRepository.save(model);
        return modelMapper.toModelDTO(model);
    }

    @Override
    public List<ModelDTO> getByBrand(Long brandId) {
        List<Model> models = modelRepository.findByBrandId(brandId);
        return models.stream()
                .map(modelMapper::toModelDTO)
                .collect(Collectors.toList());
    }
    @Override
    public Model getById(Long id) {
        return modelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Model", id));
    }

    @Override
    public ModelDTO getModelById(Long id) {
        Model model = getById(id);
        return modelMapper.toModelDTO(model);
    }

    @Override
    public PageDTO getModels(Map<String, String> params) {
        Pageable pageable = PageUtils.getPageable(params);

        ModelFilter modelFilter = new ModelFilter();
        // ... keep your existing logic to populate modelFilter ...
        if(params.containsKey("modelId")) {
            modelFilter.setModelId(MapUtils.getLong(params, "modelId"));
        }
        // (rest of your filter logic here)

        ModelSpec modelSpec = new ModelSpec(modelFilter);
        Page<Model> page = modelRepository.findAll(modelSpec, pageable);

        // FIX: Map Entity Page to DTO Page, then wrap in PageDTO
        Page<ModelDTO> dtoPage = page.map(modelMapper::toModelDTO);

        return new PageDTO(dtoPage);
    }


}
