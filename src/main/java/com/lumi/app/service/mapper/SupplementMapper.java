package com.lumi.app.service.mapper;

import com.lumi.app.domain.Supplement;
import com.lumi.app.service.dto.SupplementDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Supplement} and its DTO {@link SupplementDTO}.
 */
@Mapper(componentModel = "spring")
public interface SupplementMapper extends EntityMapper<SupplementDTO, Supplement> {}
