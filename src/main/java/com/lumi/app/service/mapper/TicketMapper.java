package com.lumi.app.service.mapper;

import com.lumi.app.domain.Ticket;
import com.lumi.app.service.dto.TicketDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Ticket} and its DTO {@link TicketDTO}.
 */
@Mapper(componentModel = "spring")
public interface TicketMapper extends EntityMapper<TicketDTO, Ticket> {}
