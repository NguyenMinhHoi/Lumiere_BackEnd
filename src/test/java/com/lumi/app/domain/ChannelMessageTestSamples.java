package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ChannelMessageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ChannelMessage getChannelMessageSample1() {
        return new ChannelMessage().id(1L).ticketId(1L).authorId(1L).externalMessageId("externalMessageId1");
    }

    public static ChannelMessage getChannelMessageSample2() {
        return new ChannelMessage().id(2L).ticketId(2L).authorId(2L).externalMessageId("externalMessageId2");
    }

    public static ChannelMessage getChannelMessageRandomSampleGenerator() {
        return new ChannelMessage()
            .id(longCount.incrementAndGet())
            .ticketId(longCount.incrementAndGet())
            .authorId(longCount.incrementAndGet())
            .externalMessageId(UUID.randomUUID().toString());
    }
}
