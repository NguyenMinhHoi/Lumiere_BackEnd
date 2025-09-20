package com.lumi.app.service.mapper;

import static com.lumi.app.domain.ClothProductMapAsserts.*;
import static com.lumi.app.domain.ClothProductMapTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClothProductMapMapperTest {

    private ClothProductMapMapper clothProductMapMapper;

    @BeforeEach
    void setUp() {
        clothProductMapMapper = new ClothProductMapMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getClothProductMapSample1();
        var actual = clothProductMapMapper.toEntity(clothProductMapMapper.toDto(expected));
        assertClothProductMapAllPropertiesEquals(expected, actual);
    }
}
