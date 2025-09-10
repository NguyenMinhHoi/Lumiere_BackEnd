package com.lumi.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChannelMessageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChannelMessageDTO.class);
        ChannelMessageDTO channelMessageDTO1 = new ChannelMessageDTO();
        channelMessageDTO1.setId(1L);
        ChannelMessageDTO channelMessageDTO2 = new ChannelMessageDTO();
        assertThat(channelMessageDTO1).isNotEqualTo(channelMessageDTO2);
        channelMessageDTO2.setId(channelMessageDTO1.getId());
        assertThat(channelMessageDTO1).isEqualTo(channelMessageDTO2);
        channelMessageDTO2.setId(2L);
        assertThat(channelMessageDTO1).isNotEqualTo(channelMessageDTO2);
        channelMessageDTO1.setId(null);
        assertThat(channelMessageDTO1).isNotEqualTo(channelMessageDTO2);
    }
}
