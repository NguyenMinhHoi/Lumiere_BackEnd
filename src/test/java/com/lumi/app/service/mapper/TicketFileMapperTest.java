package com.lumi.app.service.mapper;

import static com.lumi.app.domain.TicketFileAsserts.*;
import static com.lumi.app.domain.TicketFileTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TicketFileMapperTest {

    private TicketFileMapper ticketFileMapper;

    @BeforeEach
    void setUp() {
        ticketFileMapper = new TicketFileMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTicketFileSample1();
        var actual = ticketFileMapper.toEntity(ticketFileMapper.toDto(expected));
        assertTicketFileAllPropertiesEquals(expected, actual);
    }
}
