package com.lumi.app.domain;

import static com.lumi.app.domain.TicketFileTestSamples.*;
import static com.lumi.app.domain.TicketTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TicketFileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TicketFile.class);
        TicketFile ticketFile1 = getTicketFileSample1();
        TicketFile ticketFile2 = new TicketFile();
        assertThat(ticketFile1).isNotEqualTo(ticketFile2);

        ticketFile2.setId(ticketFile1.getId());
        assertThat(ticketFile1).isEqualTo(ticketFile2);

        ticketFile2 = getTicketFileSample2();
        assertThat(ticketFile1).isNotEqualTo(ticketFile2);
    }

    @Test
    void ticketTest() {
        TicketFile ticketFile = getTicketFileRandomSampleGenerator();
        Ticket ticketBack = getTicketRandomSampleGenerator();

        ticketFile.setTicket(ticketBack);
        assertThat(ticketFile.getTicket()).isEqualTo(ticketBack);

        ticketFile.ticket(null);
        assertThat(ticketFile.getTicket()).isNull();
    }
}
