package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class KnowledgeCategoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static KnowledgeCategory getKnowledgeCategorySample1() {
        return new KnowledgeCategory().id(1L).name("name1").slug("slug1");
    }

    public static KnowledgeCategory getKnowledgeCategorySample2() {
        return new KnowledgeCategory().id(2L).name("name2").slug("slug2");
    }

    public static KnowledgeCategory getKnowledgeCategoryRandomSampleGenerator() {
        return new KnowledgeCategory()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .slug(UUID.randomUUID().toString());
    }
}
