package com.phoneshop.service;

import com.phoneshop.dto.ColorDTO;
import com.phoneshop.entity.Color;

public interface ColorService {
    ColorDTO create(ColorDTO color);
    Color getById(Long id);
}
