package com.lumi.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SurveyResponseTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static SurveyResponse getSurveyResponseSample1() {
        return new SurveyResponse().id(1L).surveyId(1L).customerId(1L).ticketId(1L).score(1);
    }

    public static SurveyResponse getSurveyResponseSample2() {
        return new SurveyResponse().id(2L).surveyId(2L).customerId(2L).ticketId(2L).score(2);
    }

    public static SurveyResponse getSurveyResponseRandomSampleGenerator() {
        return new SurveyResponse()
            .id(longCount.incrementAndGet())
            .surveyId(longCount.incrementAndGet())
            .customerId(longCount.incrementAndGet())
            .ticketId(longCount.incrementAndGet())
            .score(intCount.incrementAndGet());
    }
}
