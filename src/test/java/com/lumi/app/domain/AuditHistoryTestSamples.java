package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AuditHistoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AuditHistory getAuditHistorySample1() {
        return new AuditHistory()
            .id(1L)
            .entityName("entityName1")
            .entityId("entityId1")
            .performedBy("performedBy1")
            .ipAddress("ipAddress1");
    }

    public static AuditHistory getAuditHistorySample2() {
        return new AuditHistory()
            .id(2L)
            .entityName("entityName2")
            .entityId("entityId2")
            .performedBy("performedBy2")
            .ipAddress("ipAddress2");
    }

    public static AuditHistory getAuditHistoryRandomSampleGenerator() {
        return new AuditHistory()
            .id(longCount.incrementAndGet())
            .entityName(UUID.randomUUID().toString())
            .entityId(UUID.randomUUID().toString())
            .performedBy(UUID.randomUUID().toString())
            .ipAddress(UUID.randomUUID().toString());
    }
}
