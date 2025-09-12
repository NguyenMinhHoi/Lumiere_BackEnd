package com.lumi.app.domain;

import static com.lumi.app.domain.TicketTagTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TicketTagTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TicketTag.class);
        TicketTag ticketTag1 = getTicketTagSample1();
        TicketTag ticketTag2 = new TicketTag();
        assertThat(ticketTag1).isNotEqualTo(ticketTag2);

        ticketTag2.setId(ticketTag1.getId());
        assertThat(ticketTag1).isEqualTo(ticketTag2);

        ticketTag2 = getTicketTagSample2();
        assertThat(ticketTag1).isNotEqualTo(ticketTag2);
    }
}
