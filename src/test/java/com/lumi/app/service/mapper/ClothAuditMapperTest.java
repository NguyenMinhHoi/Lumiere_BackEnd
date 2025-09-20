package com.lumi.app.service.mapper;

import static com.lumi.app.domain.ClothAuditAsserts.*;
import static com.lumi.app.domain.ClothAuditTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClothAuditMapperTest {

    private ClothAuditMapper clothAuditMapper;

    @BeforeEach
    void setUp() {
        clothAuditMapper = new ClothAuditMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getClothAuditSample1();
        var actual = clothAuditMapper.toEntity(clothAuditMapper.toDto(expected));
        assertClothAuditAllPropertiesEquals(expected, actual);
    }
}
