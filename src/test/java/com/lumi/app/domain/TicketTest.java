package com.lumi.app.domain;

import static com.lumi.app.domain.CustomerTestSamples.*;
import static com.lumi.app.domain.OrdersTestSamples.*;
import static com.lumi.app.domain.SlaPlanTestSamples.*;
import static com.lumi.app.domain.TagTestSamples.*;
import static com.lumi.app.domain.TicketTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TicketTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Ticket.class);
        Ticket ticket1 = getTicketSample1();
        Ticket ticket2 = new Ticket();
        assertThat(ticket1).isNotEqualTo(ticket2);

        ticket2.setId(ticket1.getId());
        assertThat(ticket1).isEqualTo(ticket2);

        ticket2 = getTicketSample2();
        assertThat(ticket1).isNotEqualTo(ticket2);
    }

    @Test
    void customerTest() {
        Ticket ticket = getTicketRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        ticket.setCustomer(customerBack);
        assertThat(ticket.getCustomer()).isEqualTo(customerBack);

        ticket.customer(null);
        assertThat(ticket.getCustomer()).isNull();
    }

    @Test
    void slaPlanTest() {
        Ticket ticket = getTicketRandomSampleGenerator();
        SlaPlan slaPlanBack = getSlaPlanRandomSampleGenerator();

        ticket.setSlaPlan(slaPlanBack);
        assertThat(ticket.getSlaPlan()).isEqualTo(slaPlanBack);

        ticket.slaPlan(null);
        assertThat(ticket.getSlaPlan()).isNull();
    }

    @Test
    void orderTest() {
        Ticket ticket = getTicketRandomSampleGenerator();
        Orders ordersBack = getOrdersRandomSampleGenerator();

        ticket.setOrder(ordersBack);
        assertThat(ticket.getOrder()).isEqualTo(ordersBack);

        ticket.order(null);
        assertThat(ticket.getOrder()).isNull();
    }

    @Test
    void tagsTest() {
        Ticket ticket = getTicketRandomSampleGenerator();
        Tag tagBack = getTagRandomSampleGenerator();

        ticket.addTags(tagBack);
        assertThat(ticket.getTags()).containsOnly(tagBack);

        ticket.removeTags(tagBack);
        assertThat(ticket.getTags()).doesNotContain(tagBack);

        ticket.tags(new HashSet<>(Set.of(tagBack)));
        assertThat(ticket.getTags()).containsOnly(tagBack);

        ticket.setTags(new HashSet<>());
        assertThat(ticket.getTags()).doesNotContain(tagBack);
    }
}
