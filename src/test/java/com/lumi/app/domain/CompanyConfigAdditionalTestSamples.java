package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CompanyConfigAdditionalTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CompanyConfigAdditional getCompanyConfigAdditionalSample1() {
        return new CompanyConfigAdditional().id(1L).companyConfigId(1L).configKey("configKey1").configValue("configValue1");
    }

    public static CompanyConfigAdditional getCompanyConfigAdditionalSample2() {
        return new CompanyConfigAdditional().id(2L).companyConfigId(2L).configKey("configKey2").configValue("configValue2");
    }

    public static CompanyConfigAdditional getCompanyConfigAdditionalRandomSampleGenerator() {
        return new CompanyConfigAdditional()
            .id(longCount.incrementAndGet())
            .companyConfigId(longCount.incrementAndGet())
            .configKey(UUID.randomUUID().toString())
            .configValue(UUID.randomUUID().toString());
    }
}
