package com.lumi.app.service.mapper;

import com.lumi.app.domain.AppConfig;
import com.lumi.app.service.dto.AppConfigDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AppConfig} and its DTO {@link AppConfigDTO}.
 */
@Mapper(componentModel = "spring")
public interface AppConfigMapper extends EntityMapper<AppConfigDTO, AppConfig> {}
