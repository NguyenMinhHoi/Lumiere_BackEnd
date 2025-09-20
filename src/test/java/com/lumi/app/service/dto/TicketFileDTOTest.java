package com.lumi.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TicketFileDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TicketFileDTO.class);
        TicketFileDTO ticketFileDTO1 = new TicketFileDTO();
        ticketFileDTO1.setId(1L);
        TicketFileDTO ticketFileDTO2 = new TicketFileDTO();
        assertThat(ticketFileDTO1).isNotEqualTo(ticketFileDTO2);
        ticketFileDTO2.setId(ticketFileDTO1.getId());
        assertThat(ticketFileDTO1).isEqualTo(ticketFileDTO2);
        ticketFileDTO2.setId(2L);
        assertThat(ticketFileDTO1).isNotEqualTo(ticketFileDTO2);
        ticketFileDTO1.setId(null);
        assertThat(ticketFileDTO1).isNotEqualTo(ticketFileDTO2);
    }
}
