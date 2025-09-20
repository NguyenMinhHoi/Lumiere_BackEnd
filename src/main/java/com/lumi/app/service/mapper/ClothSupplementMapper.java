package com.lumi.app.service.mapper;

import com.lumi.app.domain.ClothSupplement;
import com.lumi.app.service.dto.ClothSupplementDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ClothSupplement} and its DTO {@link ClothSupplementDTO}.
 */
@Mapper(componentModel = "spring")
public interface ClothSupplementMapper extends EntityMapper<ClothSupplementDTO, ClothSupplement> {}
