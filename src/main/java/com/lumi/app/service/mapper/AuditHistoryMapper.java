package com.lumi.app.service.mapper;

import com.lumi.app.domain.AuditHistory;
import com.lumi.app.service.dto.AuditHistoryDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link AuditHistory} and its DTO {@link AuditHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface AuditHistoryMapper extends EntityMapper<AuditHistoryDTO, AuditHistory> {}
