package com.lumi.app.domain;

import static com.lumi.app.domain.KnowledgeArticleTestSamples.*;
import static com.lumi.app.domain.KnowledgeCategoryTestSamples.*;
import static com.lumi.app.domain.TagTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
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

    @Test
    void categoryTest() {
        KnowledgeArticle knowledgeArticle = getKnowledgeArticleRandomSampleGenerator();
        KnowledgeCategory knowledgeCategoryBack = getKnowledgeCategoryRandomSampleGenerator();

        knowledgeArticle.setCategory(knowledgeCategoryBack);
        assertThat(knowledgeArticle.getCategory()).isEqualTo(knowledgeCategoryBack);

        knowledgeArticle.category(null);
        assertThat(knowledgeArticle.getCategory()).isNull();
    }

    @Test
    void tagsTest() {
        KnowledgeArticle knowledgeArticle = getKnowledgeArticleRandomSampleGenerator();
        Tag tagBack = getTagRandomSampleGenerator();

        knowledgeArticle.addTags(tagBack);
        assertThat(knowledgeArticle.getTags()).containsOnly(tagBack);

        knowledgeArticle.removeTags(tagBack);
        assertThat(knowledgeArticle.getTags()).doesNotContain(tagBack);

        knowledgeArticle.tags(new HashSet<>(Set.of(tagBack)));
        assertThat(knowledgeArticle.getTags()).containsOnly(tagBack);

        knowledgeArticle.setTags(new HashSet<>());
        assertThat(knowledgeArticle.getTags()).doesNotContain(tagBack);
    }
}
