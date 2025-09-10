package com.lumi.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class KnowledgeArticleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static KnowledgeArticle getKnowledgeArticleSample1() {
        return new KnowledgeArticle().id(1L).title("title1").views(1L);
    }

    public static KnowledgeArticle getKnowledgeArticleSample2() {
        return new KnowledgeArticle().id(2L).title("title2").views(2L);
    }

    public static KnowledgeArticle getKnowledgeArticleRandomSampleGenerator() {
        return new KnowledgeArticle()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .views(longCount.incrementAndGet());
    }
}
