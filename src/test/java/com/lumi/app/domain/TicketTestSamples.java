package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TicketTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Ticket getTicketSample1() {
        return new Ticket().id(1L).code("code1").subject("subject1");
    }

    public static Ticket getTicketSample2() {
        return new Ticket().id(2L).code("code2").subject("subject2");
    }

    public static Ticket getTicketRandomSampleGenerator() {
        return new Ticket().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString()).subject(UUID.randomUUID().toString());
    }
}
