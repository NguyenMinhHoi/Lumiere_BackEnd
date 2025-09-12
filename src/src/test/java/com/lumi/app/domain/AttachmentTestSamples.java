package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AttachmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Attachment getAttachmentSample1() {
        return new Attachment().id(1L).ticketId(1L).commentId(1L).name("name1").url("url1").contentType("contentType1").size(1L);
    }

    public static Attachment getAttachmentSample2() {
        return new Attachment().id(2L).ticketId(2L).commentId(2L).name("name2").url("url2").contentType("contentType2").size(2L);
    }

    public static Attachment getAttachmentRandomSampleGenerator() {
        return new Attachment()
            .id(longCount.incrementAndGet())
            .ticketId(longCount.incrementAndGet())
            .commentId(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .url(UUID.randomUUID().toString())
            .contentType(UUID.randomUUID().toString())
            .size(longCount.incrementAndGet());
    }
}
