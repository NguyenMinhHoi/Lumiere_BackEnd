package com.lumi.app.service.mapper;

import com.lumi.app.domain.TicketFile;
import com.lumi.app.service.dto.TicketFileDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link TicketFile} and its DTO {@link TicketFileDTO}.
 */
@Mapper(componentModel = "spring")
public interface TicketFileMapper extends EntityMapper<TicketFileDTO, TicketFile> {}
