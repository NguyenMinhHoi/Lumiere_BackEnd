package com.lumi.app.domain;

import static com.lumi.app.domain.ArticleTagTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ArticleTagTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ArticleTag.class);
        ArticleTag articleTag1 = getArticleTagSample1();
        ArticleTag articleTag2 = new ArticleTag();
        assertThat(articleTag1).isNotEqualTo(articleTag2);

        articleTag2.setId(articleTag1.getId());
        assertThat(articleTag1).isEqualTo(articleTag2);

        articleTag2 = getArticleTagSample2();
        assertThat(articleTag1).isNotEqualTo(articleTag2);
    }
}
