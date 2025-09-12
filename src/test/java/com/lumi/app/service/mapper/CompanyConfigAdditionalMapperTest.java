package com.lumi.app.service.mapper;

import static com.lumi.app.domain.CompanyConfigAdditionalAsserts.*;
import static com.lumi.app.domain.CompanyConfigAdditionalTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CompanyConfigAdditionalMapperTest {

    private CompanyConfigAdditionalMapper companyConfigAdditionalMapper;

    @BeforeEach
    void setUp() {
        companyConfigAdditionalMapper = new CompanyConfigAdditionalMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCompanyConfigAdditionalSample1();
        var actual = companyConfigAdditionalMapper.toEntity(companyConfigAdditionalMapper.toDto(expected));
        assertCompanyConfigAdditionalAllPropertiesEquals(expected, actual);
    }
}
