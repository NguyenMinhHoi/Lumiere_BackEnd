package com.lumi.app.service.mapper;

import static com.lumi.app.domain.CompanyConfigAsserts.*;
import static com.lumi.app.domain.CompanyConfigTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CompanyConfigMapperTest {

    private CompanyConfigMapper companyConfigMapper;

    @BeforeEach
    void setUp() {
        companyConfigMapper = new CompanyConfigMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCompanyConfigSample1();
        var actual = companyConfigMapper.toEntity(companyConfigMapper.toDto(expected));
        assertCompanyConfigAllPropertiesEquals(expected, actual);
    }
}
