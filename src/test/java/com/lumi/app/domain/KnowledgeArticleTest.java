package com.lumi.app.domain;

import static com.lumi.app.domain.KnowledgeArticleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class KnowledgeArticleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(KnowledgeArticle.class);
        KnowledgeArticle knowledgeArticle1 = getKnowledgeArticleSample1();
        KnowledgeArticle knowledgeArticle2 = new KnowledgeArticle();
        assertThat(knowledgeArticle1).isNotEqualTo(knowledgeArticle2);

        knowledgeArticle2.setId(knowledgeArticle1.getId());
        assertThat(knowledgeArticle1).isEqualTo(knowledgeArticle2);

        knowledgeArticle2 = getKnowledgeArticleSample2();
        assertThat(knowledgeArticle1).isNotEqualTo(knowledgeArticle2);
    }
}
