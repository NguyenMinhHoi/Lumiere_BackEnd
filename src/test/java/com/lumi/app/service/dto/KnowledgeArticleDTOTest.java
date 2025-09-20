package com.lumi.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class KnowledgeArticleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(KnowledgeArticleDTO.class);
        KnowledgeArticleDTO knowledgeArticleDTO1 = new KnowledgeArticleDTO();
        knowledgeArticleDTO1.setId(1L);
        KnowledgeArticleDTO knowledgeArticleDTO2 = new KnowledgeArticleDTO();
        assertThat(knowledgeArticleDTO1).isNotEqualTo(knowledgeArticleDTO2);
        knowledgeArticleDTO2.setId(knowledgeArticleDTO1.getId());
        assertThat(knowledgeArticleDTO1).isEqualTo(knowledgeArticleDTO2);
        knowledgeArticleDTO2.setId(2L);
        assertThat(knowledgeArticleDTO1).isNotEqualTo(knowledgeArticleDTO2);
        knowledgeArticleDTO1.setId(null);
        assertThat(knowledgeArticleDTO1).isNotEqualTo(knowledgeArticleDTO2);
    }
}
