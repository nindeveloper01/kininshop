package com.phoneshop.repository;

import com.phoneshop.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ColorRepository extends JpaRepository<Color, Long> {

}
