package com.lumi.app.domain;

import static com.lumi.app.domain.KnowledgeArticleTestSamples.*;
import static com.lumi.app.domain.TagTestSamples.*;
import static com.lumi.app.domain.TicketTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TagTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tag.class);
        Tag tag1 = getTagSample1();
        Tag tag2 = new Tag();
        assertThat(tag1).isNotEqualTo(tag2);

        tag2.setId(tag1.getId());
        assertThat(tag1).isEqualTo(tag2);

        tag2 = getTagSample2();
        assertThat(tag1).isNotEqualTo(tag2);
    }

    @Test
    void ticketsTest() {
        Tag tag = getTagRandomSampleGenerator();
        Ticket ticketBack = getTicketRandomSampleGenerator();

        tag.addTickets(ticketBack);
        assertThat(tag.getTickets()).containsOnly(ticketBack);
        assertThat(ticketBack.getTags()).containsOnly(tag);

        tag.removeTickets(ticketBack);
        assertThat(tag.getTickets()).doesNotContain(ticketBack);
        assertThat(ticketBack.getTags()).doesNotContain(tag);

        tag.tickets(new HashSet<>(Set.of(ticketBack)));
        assertThat(tag.getTickets()).containsOnly(ticketBack);
        assertThat(ticketBack.getTags()).containsOnly(tag);

        tag.setTickets(new HashSet<>());
        assertThat(tag.getTickets()).doesNotContain(ticketBack);
        assertThat(ticketBack.getTags()).doesNotContain(tag);
    }

    @Test
    void articlesTest() {
        Tag tag = getTagRandomSampleGenerator();
        KnowledgeArticle knowledgeArticleBack = getKnowledgeArticleRandomSampleGenerator();

        tag.addArticles(knowledgeArticleBack);
        assertThat(tag.getArticles()).containsOnly(knowledgeArticleBack);
        assertThat(knowledgeArticleBack.getTags()).containsOnly(tag);

        tag.removeArticles(knowledgeArticleBack);
        assertThat(tag.getArticles()).doesNotContain(knowledgeArticleBack);
        assertThat(knowledgeArticleBack.getTags()).doesNotContain(tag);

        tag.articles(new HashSet<>(Set.of(knowledgeArticleBack)));
        assertThat(tag.getArticles()).containsOnly(knowledgeArticleBack);
        assertThat(knowledgeArticleBack.getTags()).containsOnly(tag);

        tag.setArticles(new HashSet<>());
        assertThat(tag.getArticles()).doesNotContain(knowledgeArticleBack);
        assertThat(knowledgeArticleBack.getTags()).doesNotContain(tag);
    }
}
