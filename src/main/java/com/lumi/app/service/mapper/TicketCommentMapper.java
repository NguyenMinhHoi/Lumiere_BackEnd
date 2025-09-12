package com.lumi.app.service.mapper;

import com.lumi.app.domain.TicketComment;
import com.lumi.app.service.dto.TicketCommentDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link TicketComment} and its DTO {@link TicketCommentDTO}.
 */
@Mapper(componentModel = "spring")
public interface TicketCommentMapper extends EntityMapper<TicketCommentDTO, TicketComment> {}
