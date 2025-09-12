package com.lumi.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CompanyConfigTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CompanyConfig getCompanyConfigSample1() {
        return new CompanyConfig().id(1L).companyId(1L).appId(1L);
    }

    public static CompanyConfig getCompanyConfigSample2() {
        return new CompanyConfig().id(2L).companyId(2L).appId(2L);
    }

    public static CompanyConfig getCompanyConfigRandomSampleGenerator() {
        return new CompanyConfig()
            .id(longCount.incrementAndGet())
            .companyId(longCount.incrementAndGet())
            .appId(longCount.incrementAndGet());
    }
}
