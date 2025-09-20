package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TicketTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Ticket getTicketSample1() {
        return new Ticket().id(1L).customerId(1L).slaPlanId(1L).orderId(1L).assigneeEmployeeId(1L).code("code1").subject("subject1");
    }

    public static Ticket getTicketSample2() {
        return new Ticket().id(2L).customerId(2L).slaPlanId(2L).orderId(2L).assigneeEmployeeId(2L).code("code2").subject("subject2");
    }

    public static Ticket getTicketRandomSampleGenerator() {
        return new Ticket()
            .id(longCount.incrementAndGet())
            .customerId(longCount.incrementAndGet())
            .slaPlanId(longCount.incrementAndGet())
            .orderId(longCount.incrementAndGet())
            .assigneeEmployeeId(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .subject(UUID.randomUUID().toString());
    }
}
