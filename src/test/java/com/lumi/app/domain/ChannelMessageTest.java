package com.lumi.app.domain;

import static com.lumi.app.domain.ChannelMessageTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChannelMessageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChannelMessage.class);
        ChannelMessage channelMessage1 = getChannelMessageSample1();
        ChannelMessage channelMessage2 = new ChannelMessage();
        assertThat(channelMessage1).isNotEqualTo(channelMessage2);

        channelMessage2.setId(channelMessage1.getId());
        assertThat(channelMessage1).isEqualTo(channelMessage2);

        channelMessage2 = getChannelMessageSample2();
        assertThat(channelMessage1).isNotEqualTo(channelMessage2);
    }
}
