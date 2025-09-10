package com.lumi.app.service.mapper;

import com.lumi.app.domain.Ticket;
import com.lumi.app.domain.TicketComment;
import com.lumi.app.domain.User;
import com.lumi.app.service.dto.TicketCommentDTO;
import com.lumi.app.service.dto.TicketDTO;
import com.lumi.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TicketComment} and its DTO {@link TicketCommentDTO}.
 */
@Mapper(componentModel = "spring")
public interface TicketCommentMapper extends EntityMapper<TicketCommentDTO, TicketComment> {
    @Mapping(target = "ticket", source = "ticket", qualifiedByName = "ticketCode")
    @Mapping(target = "author", source = "author", qualifiedByName = "userLogin")
    TicketCommentDTO toDto(TicketComment s);

    @Named("ticketCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    TicketDTO toDtoTicketCode(Ticket ticket);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
