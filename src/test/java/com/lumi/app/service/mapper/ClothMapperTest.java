package com.lumi.app.service.mapper;

import static com.lumi.app.domain.ClothAsserts.*;
import static com.lumi.app.domain.ClothTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClothMapperTest {

    private ClothMapper clothMapper;

    @BeforeEach
    void setUp() {
        clothMapper = new ClothMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getClothSample1();
        var actual = clothMapper.toEntity(clothMapper.toDto(expected));
        assertClothAllPropertiesEquals(expected, actual);
    }
}
