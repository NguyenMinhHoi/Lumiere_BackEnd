package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AppConfigTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AppConfig getAppConfigSample1() {
        return new AppConfig().id(1L).appCode("appCode1").configKey("configKey1").configValue("configValue1");
    }

    public static AppConfig getAppConfigSample2() {
        return new AppConfig().id(2L).appCode("appCode2").configKey("configKey2").configValue("configValue2");
    }

    public static AppConfig getAppConfigRandomSampleGenerator() {
        return new AppConfig()
            .id(longCount.incrementAndGet())
            .appCode(UUID.randomUUID().toString())
            .configKey(UUID.randomUUID().toString())
            .configValue(UUID.randomUUID().toString());
    }
}
