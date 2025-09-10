package com.lumi.app.service.mapper;

import com.lumi.app.domain.ChannelMessage;
import com.lumi.app.domain.Ticket;
import com.lumi.app.domain.User;
import com.lumi.app.service.dto.ChannelMessageDTO;
import com.lumi.app.service.dto.TicketDTO;
import com.lumi.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ChannelMessage} and its DTO {@link ChannelMessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface ChannelMessageMapper extends EntityMapper<ChannelMessageDTO, ChannelMessage> {
    @Mapping(target = "ticket", source = "ticket", qualifiedByName = "ticketCode")
    @Mapping(target = "author", source = "author", qualifiedByName = "userLogin")
    ChannelMessageDTO toDto(ChannelMessage s);

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
