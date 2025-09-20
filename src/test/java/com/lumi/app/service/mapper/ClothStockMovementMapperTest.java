package com.lumi.app.service.mapper;

import static com.lumi.app.domain.ClothStockMovementAsserts.*;
import static com.lumi.app.domain.ClothStockMovementTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClothStockMovementMapperTest {

    private ClothStockMovementMapper clothStockMovementMapper;

    @BeforeEach
    void setUp() {
        clothStockMovementMapper = new ClothStockMovementMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getClothStockMovementSample1();
        var actual = clothStockMovementMapper.toEntity(clothStockMovementMapper.toDto(expected));
        assertClothStockMovementAllPropertiesEquals(expected, actual);
    }
}
