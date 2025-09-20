package com.lumi.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class TicketTagTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TicketTag getTicketTagSample1() {
        return new TicketTag().id(1L).ticketId(1L).tagId(1L);
    }

    public static TicketTag getTicketTagSample2() {
        return new TicketTag().id(2L).ticketId(2L).tagId(2L);
    }

    public static TicketTag getTicketTagRandomSampleGenerator() {
        return new TicketTag().id(longCount.incrementAndGet()).ticketId(longCount.incrementAndGet()).tagId(longCount.incrementAndGet());
    }
}
