package com.lumi.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TicketCommentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TicketCommentDTO.class);
        TicketCommentDTO ticketCommentDTO1 = new TicketCommentDTO();
        ticketCommentDTO1.setId(1L);
        TicketCommentDTO ticketCommentDTO2 = new TicketCommentDTO();
        assertThat(ticketCommentDTO1).isNotEqualTo(ticketCommentDTO2);
        ticketCommentDTO2.setId(ticketCommentDTO1.getId());
        assertThat(ticketCommentDTO1).isEqualTo(ticketCommentDTO2);
        ticketCommentDTO2.setId(2L);
        assertThat(ticketCommentDTO1).isNotEqualTo(ticketCommentDTO2);
        ticketCommentDTO1.setId(null);
        assertThat(ticketCommentDTO1).isNotEqualTo(ticketCommentDTO2);
    }
}
