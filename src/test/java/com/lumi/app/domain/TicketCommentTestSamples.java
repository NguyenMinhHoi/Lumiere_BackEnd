package com.lumi.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class TicketCommentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TicketComment getTicketCommentSample1() {
        return new TicketComment().id(1L);
    }

    public static TicketComment getTicketCommentSample2() {
        return new TicketComment().id(2L);
    }

    public static TicketComment getTicketCommentRandomSampleGenerator() {
        return new TicketComment().id(longCount.incrementAndGet());
    }
}
