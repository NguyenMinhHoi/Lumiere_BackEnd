package com.lumi.app.service.mapper;

import static com.lumi.app.domain.ClothSupplementAsserts.*;
import static com.lumi.app.domain.ClothSupplementTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClothSupplementMapperTest {

    private ClothSupplementMapper clothSupplementMapper;

    @BeforeEach
    void setUp() {
        clothSupplementMapper = new ClothSupplementMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getClothSupplementSample1();
        var actual = clothSupplementMapper.toEntity(clothSupplementMapper.toDto(expected));
        assertClothSupplementAllPropertiesEquals(expected, actual);
    }
}
