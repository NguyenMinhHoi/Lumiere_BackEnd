package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CustomerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Customer getCustomerSample1() {
        return new Customer().id(1L).code("code1").fullName("fullName1").email("email1").phone("phone1").points(1).address("address1");
    }

    public static Customer getCustomerSample2() {
        return new Customer().id(2L).code("code2").fullName("fullName2").email("email2").phone("phone2").points(2).address("address2");
    }

    public static Customer getCustomerRandomSampleGenerator() {
        return new Customer()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .fullName(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .points(intCount.incrementAndGet())
            .address(UUID.randomUUID().toString());
    }
}
