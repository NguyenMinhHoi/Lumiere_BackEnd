package com.lumi.app.service.mapper;

import com.lumi.app.domain.ClothAudit;
import com.lumi.app.service.dto.ClothAuditDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ClothAudit} and its DTO {@link ClothAuditDTO}.
 */
@Mapper(componentModel = "spring")
public interface ClothAuditMapper extends EntityMapper<ClothAuditDTO, ClothAudit> {}
