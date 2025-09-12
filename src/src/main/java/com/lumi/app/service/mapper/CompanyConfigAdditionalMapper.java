package com.lumi.app.service.mapper;

import com.lumi.app.domain.CompanyConfigAdditional;
import com.lumi.app.service.dto.CompanyConfigAdditionalDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CompanyConfigAdditional} and its DTO {@link CompanyConfigAdditionalDTO}.
 */
@Mapper(componentModel = "spring")
public interface CompanyConfigAdditionalMapper extends EntityMapper<CompanyConfigAdditionalDTO, CompanyConfigAdditional> {}
