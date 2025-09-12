package com.lumi.app.service.mapper;

import static com.lumi.app.domain.IntegrationLogAsserts.*;
import static com.lumi.app.domain.IntegrationLogTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IntegrationLogMapperTest {

    private IntegrationLogMapper integrationLogMapper;

    @BeforeEach
    void setUp() {
        integrationLogMapper = new IntegrationLogMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getIntegrationLogSample1();
        var actual = integrationLogMapper.toEntity(integrationLogMapper.toDto(expected));
        assertIntegrationLogAllPropertiesEquals(expected, actual);
    }
}
