package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TicketFileTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TicketFile getTicketFileSample1() {
        return new TicketFile()
            .id(1L)
            .fileName("fileName1")
            .originalName("originalName1")
            .contentType("contentType1")
            .capacity(1L)
            .path("path1")
            .url("url1")
            .checksum("checksum1");
    }

    public static TicketFile getTicketFileSample2() {
        return new TicketFile()
            .id(2L)
            .fileName("fileName2")
            .originalName("originalName2")
            .contentType("contentType2")
            .capacity(2L)
            .path("path2")
            .url("url2")
            .checksum("checksum2");
    }

    public static TicketFile getTicketFileRandomSampleGenerator() {
        return new TicketFile()
            .id(longCount.incrementAndGet())
            .fileName(UUID.randomUUID().toString())
            .originalName(UUID.randomUUID().toString())
            .contentType(UUID.randomUUID().toString())
            .capacity(longCount.incrementAndGet())
            .path(UUID.randomUUID().toString())
            .url(UUID.randomUUID().toString())
            .checksum(UUID.randomUUID().toString());
    }
}
