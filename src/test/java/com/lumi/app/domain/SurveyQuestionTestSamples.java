package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SurveyQuestionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static SurveyQuestion getSurveyQuestionSample1() {
        return new SurveyQuestion().id(1L).text("text1").scaleMin(1).scaleMax(1).orderNo(1);
    }

    public static SurveyQuestion getSurveyQuestionSample2() {
        return new SurveyQuestion().id(2L).text("text2").scaleMin(2).scaleMax(2).orderNo(2);
    }

    public static SurveyQuestion getSurveyQuestionRandomSampleGenerator() {
        return new SurveyQuestion()
            .id(longCount.incrementAndGet())
            .text(UUID.randomUUID().toString())
            .scaleMin(intCount.incrementAndGet())
            .scaleMax(intCount.incrementAndGet())
            .orderNo(intCount.incrementAndGet());
    }
}
