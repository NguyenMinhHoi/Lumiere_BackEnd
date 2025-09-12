package com.lumi.app.service.mapper;

import com.lumi.app.domain.ChannelMessage;
import com.lumi.app.service.dto.ChannelMessageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ChannelMessage} and its DTO {@link ChannelMessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface ChannelMessageMapper extends EntityMapper<ChannelMessageDTO, ChannelMessage> {}
