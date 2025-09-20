package com.lumi.app.service.mapper;

import static com.lumi.app.domain.ChannelMessageAsserts.*;
import static com.lumi.app.domain.ChannelMessageTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChannelMessageMapperTest {

    private ChannelMessageMapper channelMessageMapper;

    @BeforeEach
    void setUp() {
        channelMessageMapper = new ChannelMessageMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getChannelMessageSample1();
        var actual = channelMessageMapper.toEntity(channelMessageMapper.toDto(expected));
        assertChannelMessageAllPropertiesEquals(expected, actual);
    }
}
