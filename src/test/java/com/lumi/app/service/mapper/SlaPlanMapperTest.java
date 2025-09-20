package com.lumi.app.service.mapper;

import static com.lumi.app.domain.SlaPlanAsserts.*;
import static com.lumi.app.domain.SlaPlanTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SlaPlanMapperTest {

    private SlaPlanMapper slaPlanMapper;

    @BeforeEach
    void setUp() {
        slaPlanMapper = new SlaPlanMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSlaPlanSample1();
        var actual = slaPlanMapper.toEntity(slaPlanMapper.toDto(expected));
        assertSlaPlanAllPropertiesEquals(expected, actual);
    }
}
