package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class NotificationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Notification getNotificationSample1() {
        return new Notification().id(1L).ticketId(1L).customerId(1L).surveyId(1L).subject("subject1").retryCount(1);
    }

    public static Notification getNotificationSample2() {
        return new Notification().id(2L).ticketId(2L).customerId(2L).surveyId(2L).subject("subject2").retryCount(2);
    }

    public static Notification getNotificationRandomSampleGenerator() {
        return new Notification()
            .id(longCount.incrementAndGet())
            .ticketId(longCount.incrementAndGet())
            .customerId(longCount.incrementAndGet())
            .surveyId(longCount.incrementAndGet())
            .subject(UUID.randomUUID().toString())
            .retryCount(intCount.incrementAndGet());
    }
}
