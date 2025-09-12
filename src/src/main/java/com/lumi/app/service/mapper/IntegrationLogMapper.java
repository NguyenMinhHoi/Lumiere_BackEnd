package com.lumi.app.service.mapper;

import com.lumi.app.domain.IntegrationLog;
import com.lumi.app.service.dto.IntegrationLogDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link IntegrationLog} and its DTO {@link IntegrationLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface IntegrationLogMapper extends EntityMapper<IntegrationLogDTO, IntegrationLog> {}
