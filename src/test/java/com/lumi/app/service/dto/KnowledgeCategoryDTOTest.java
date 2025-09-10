package com.lumi.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class KnowledgeCategoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(KnowledgeCategoryDTO.class);
        KnowledgeCategoryDTO knowledgeCategoryDTO1 = new KnowledgeCategoryDTO();
        knowledgeCategoryDTO1.setId(1L);
        KnowledgeCategoryDTO knowledgeCategoryDTO2 = new KnowledgeCategoryDTO();
        assertThat(knowledgeCategoryDTO1).isNotEqualTo(knowledgeCategoryDTO2);
        knowledgeCategoryDTO2.setId(knowledgeCategoryDTO1.getId());
        assertThat(knowledgeCategoryDTO1).isEqualTo(knowledgeCategoryDTO2);
        knowledgeCategoryDTO2.setId(2L);
        assertThat(knowledgeCategoryDTO1).isNotEqualTo(knowledgeCategoryDTO2);
        knowledgeCategoryDTO1.setId(null);
        assertThat(knowledgeCategoryDTO1).isNotEqualTo(knowledgeCategoryDTO2);
    }
}
