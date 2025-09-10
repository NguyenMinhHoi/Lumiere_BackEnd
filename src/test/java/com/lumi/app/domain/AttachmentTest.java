package com.lumi.app.domain;

import static com.lumi.app.domain.AttachmentTestSamples.*;
import static com.lumi.app.domain.TicketCommentTestSamples.*;
import static com.lumi.app.domain.TicketTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AttachmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Attachment.class);
        Attachment attachment1 = getAttachmentSample1();
        Attachment attachment2 = new Attachment();
        assertThat(attachment1).isNotEqualTo(attachment2);

        attachment2.setId(attachment1.getId());
        assertThat(attachment1).isEqualTo(attachment2);

        attachment2 = getAttachmentSample2();
        assertThat(attachment1).isNotEqualTo(attachment2);
    }

    @Test
    void ticketTest() {
        Attachment attachment = getAttachmentRandomSampleGenerator();
        Ticket ticketBack = getTicketRandomSampleGenerator();

        attachment.setTicket(ticketBack);
        assertThat(attachment.getTicket()).isEqualTo(ticketBack);

        attachment.ticket(null);
        assertThat(attachment.getTicket()).isNull();
    }

    @Test
    void commentTest() {
        Attachment attachment = getAttachmentRandomSampleGenerator();
        TicketComment ticketCommentBack = getTicketCommentRandomSampleGenerator();

        attachment.setComment(ticketCommentBack);
        assertThat(attachment.getComment()).isEqualTo(ticketCommentBack);

        attachment.comment(null);
        assertThat(attachment.getComment()).isNull();
    }
}
