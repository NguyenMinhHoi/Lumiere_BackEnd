package com.lumi.app.service.mapper;

import static com.lumi.app.domain.IntegrationWebhookAsserts.*;
import static com.lumi.app.domain.IntegrationWebhookTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IntegrationWebhookMapperTest {

    private IntegrationWebhookMapper integrationWebhookMapper;

    @BeforeEach
    void setUp() {
        integrationWebhookMapper = new IntegrationWebhookMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getIntegrationWebhookSample1();
        var actual = integrationWebhookMapper.toEntity(integrationWebhookMapper.toDto(expected));
        assertIntegrationWebhookAllPropertiesEquals(expected, actual);
    }
}
