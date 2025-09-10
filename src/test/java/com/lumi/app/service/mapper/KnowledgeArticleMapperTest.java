package com.lumi.app.service.mapper;

import static com.lumi.app.domain.KnowledgeArticleAsserts.*;
import static com.lumi.app.domain.KnowledgeArticleTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KnowledgeArticleMapperTest {

    private KnowledgeArticleMapper knowledgeArticleMapper;

    @BeforeEach
    void setUp() {
        knowledgeArticleMapper = new KnowledgeArticleMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getKnowledgeArticleSample1();
        var actual = knowledgeArticleMapper.toEntity(knowledgeArticleMapper.toDto(expected));
        assertKnowledgeArticleAllPropertiesEquals(expected, actual);
    }
}
