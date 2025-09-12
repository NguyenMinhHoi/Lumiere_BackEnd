package com.lumi.app.service.mapper;

import com.lumi.app.domain.CompanyConfig;
import com.lumi.app.service.dto.CompanyConfigDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CompanyConfig} and its DTO {@link CompanyConfigDTO}.
 */
@Mapper(componentModel = "spring")
public interface CompanyConfigMapper extends EntityMapper<CompanyConfigDTO, CompanyConfig> {}
