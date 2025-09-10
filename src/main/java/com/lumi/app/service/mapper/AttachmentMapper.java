package com.lumi.app.service.mapper;

import com.lumi.app.domain.Attachment;
import com.lumi.app.domain.Ticket;
import com.lumi.app.domain.TicketComment;
import com.lumi.app.service.dto.AttachmentDTO;
import com.lumi.app.service.dto.TicketCommentDTO;
import com.lumi.app.service.dto.TicketDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Attachment} and its DTO {@link AttachmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface AttachmentMapper extends EntityMapper<AttachmentDTO, Attachment> {
    @Mapping(target = "ticket", source = "ticket", qualifiedByName = "ticketCode")
    @Mapping(target = "comment", source = "comment", qualifiedByName = "ticketCommentId")
    AttachmentDTO toDto(Attachment s);

    @Named("ticketCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    TicketDTO toDtoTicketCode(Ticket ticket);

    @Named("ticketCommentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TicketCommentDTO toDtoTicketCommentId(TicketComment ticketComment);
}
