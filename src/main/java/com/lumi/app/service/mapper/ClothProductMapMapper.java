package com.lumi.app.service.mapper;

import com.lumi.app.domain.ClothProductMap;
import com.lumi.app.service.dto.ClothProductMapDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ClothProductMap} and its DTO {@link ClothProductMapDTO}.
 */
@Mapper(componentModel = "spring")
public interface ClothProductMapMapper extends EntityMapper<ClothProductMapDTO, ClothProductMap> {}
