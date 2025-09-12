package com.lumi.app.service.mapper;

import com.lumi.app.domain.Notification;
import com.lumi.app.service.dto.NotificationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Notification} and its DTO {@link NotificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {}
