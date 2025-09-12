package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EmployeeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Employee getEmployeeSample1() {
        return new Employee().id(1L).code("code1").fullName("fullName1").email("email1").phone("phone1").department("department1");
    }

    public static Employee getEmployeeSample2() {
        return new Employee().id(2L).code("code2").fullName("fullName2").email("email2").phone("phone2").department("department2");
    }

    public static Employee getEmployeeRandomSampleGenerator() {
        return new Employee()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .fullName(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .department(UUID.randomUUID().toString());
    }
}
