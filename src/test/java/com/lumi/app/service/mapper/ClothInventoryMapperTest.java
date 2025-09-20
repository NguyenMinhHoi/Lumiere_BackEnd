package com.lumi.app.service.mapper;

import static com.lumi.app.domain.ClothInventoryAsserts.*;
import static com.lumi.app.domain.ClothInventoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClothInventoryMapperTest {

    private ClothInventoryMapper clothInventoryMapper;

    @BeforeEach
    void setUp() {
        clothInventoryMapper = new ClothInventoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getClothInventorySample1();
        var actual = clothInventoryMapper.toEntity(clothInventoryMapper.toDto(expected));
        assertClothInventoryAllPropertiesEquals(expected, actual);
    }
}
