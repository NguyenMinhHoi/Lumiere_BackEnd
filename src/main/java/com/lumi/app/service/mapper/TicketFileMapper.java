package com.lumi.app.service.mapper;

import com.lumi.app.domain.Ticket;
import com.lumi.app.domain.TicketFile;
import com.lumi.app.domain.User;
import com.lumi.app.service.dto.TicketDTO;
import com.lumi.app.service.dto.TicketFileDTO;
import com.lumi.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TicketFile} and its DTO {@link TicketFileDTO}.
 */
@Mapper(componentModel = "spring")
public interface TicketFileMapper extends EntityMapper<TicketFileDTO, TicketFile> {
    @Mapping(target = "ticket", source = "ticket", qualifiedByName = "ticketCode")
    @Mapping(target = "uploader", source = "uploader", qualifiedByName = "userLogin")
    TicketFileDTO toDto(TicketFile s);

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
