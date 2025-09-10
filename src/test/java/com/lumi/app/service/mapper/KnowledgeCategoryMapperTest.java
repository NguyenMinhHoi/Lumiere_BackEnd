package com.lumi.app.service.mapper;

import static com.lumi.app.domain.KnowledgeCategoryAsserts.*;
import static com.lumi.app.domain.KnowledgeCategoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KnowledgeCategoryMapperTest {

    private KnowledgeCategoryMapper knowledgeCategoryMapper;

    @BeforeEach
    void setUp() {
        knowledgeCategoryMapper = new KnowledgeCategoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getKnowledgeCategorySample1();
        var actual = knowledgeCategoryMapper.toEntity(knowledgeCategoryMapper.toDto(expected));
        assertKnowledgeCategoryAllPropertiesEquals(expected, actual);
    }
}
