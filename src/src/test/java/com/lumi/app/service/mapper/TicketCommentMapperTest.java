package com.lumi.app.service.mapper;

import static com.lumi.app.domain.TicketCommentAsserts.*;
import static com.lumi.app.domain.TicketCommentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TicketCommentMapperTest {

    private TicketCommentMapper ticketCommentMapper;

    @BeforeEach
    void setUp() {
        ticketCommentMapper = new TicketCommentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTicketCommentSample1();
        var actual = ticketCommentMapper.toEntity(ticketCommentMapper.toDto(expected));
        assertTicketCommentAllPropertiesEquals(expected, actual);
    }
}
