package com.phoneshop.service.impl;

import com.phoneshop.dto.ColorDTO;
import com.phoneshop.entity.Color;
import com.phoneshop.mapper.ColorMapper;
import com.phoneshop.repository.ColorRepository;
import com.phoneshop.service.ColorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ColorServiceImpl implements ColorService {
    private final ColorRepository colorRepository;
    private final ColorMapper colorMapper;
    @Override
    public ColorDTO create(ColorDTO color) {
        Color colorEntity = colorMapper.toColor(color);
        Color savedColor = colorRepository.save(colorEntity);

        return colorMapper.toColorDTO(savedColor);
    }

    @Override
    public Color getById(Long id) {
        return colorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Color not found with id: " + id));
    }
}
