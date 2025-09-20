package com.lumi.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ArticleTagTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ArticleTag getArticleTagSample1() {
        return new ArticleTag().id(1L).articleId(1L).tagId(1L);
    }

    public static ArticleTag getArticleTagSample2() {
        return new ArticleTag().id(2L).articleId(2L).tagId(2L);
    }

    public static ArticleTag getArticleTagRandomSampleGenerator() {
        return new ArticleTag().id(longCount.incrementAndGet()).articleId(longCount.incrementAndGet()).tagId(longCount.incrementAndGet());
    }
}
