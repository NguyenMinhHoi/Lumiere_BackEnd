package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SupplierTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Supplier getSupplierSample1() {
        return new Supplier().id(1L).code("code1").name("name1").email("email1").phone("phone1").address("address1");
    }

    public static Supplier getSupplierSample2() {
        return new Supplier().id(2L).code("code2").name("name2").email("email2").phone("phone2").address("address2");
    }

    public static Supplier getSupplierRandomSampleGenerator() {
        return new Supplier()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString());
    }
}
