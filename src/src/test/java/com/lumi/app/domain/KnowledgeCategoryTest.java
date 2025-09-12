package com.lumi.app.domain;

import static com.lumi.app.domain.KnowledgeCategoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class KnowledgeCategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(KnowledgeCategory.class);
        KnowledgeCategory knowledgeCategory1 = getKnowledgeCategorySample1();
        KnowledgeCategory knowledgeCategory2 = new KnowledgeCategory();
        assertThat(knowledgeCategory1).isNotEqualTo(knowledgeCategory2);

        knowledgeCategory2.setId(knowledgeCategory1.getId());
        assertThat(knowledgeCategory1).isEqualTo(knowledgeCategory2);

        knowledgeCategory2 = getKnowledgeCategorySample2();
        assertThat(knowledgeCategory1).isNotEqualTo(knowledgeCategory2);
    }
}
