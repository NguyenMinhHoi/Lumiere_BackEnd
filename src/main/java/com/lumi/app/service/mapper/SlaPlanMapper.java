package com.lumi.app.service.mapper;

import com.lumi.app.domain.SlaPlan;
import com.lumi.app.service.dto.SlaPlanDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link SlaPlan} and its DTO {@link SlaPlanDTO}.
 */
@Mapper(componentModel = "spring")
public interface SlaPlanMapper extends EntityMapper<SlaPlanDTO, SlaPlan> {}
