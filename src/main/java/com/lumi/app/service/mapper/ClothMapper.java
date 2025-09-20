package com.lumi.app.service.mapper;

import com.lumi.app.domain.Cloth;
import com.lumi.app.service.dto.ClothDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Cloth} and its DTO {@link ClothDTO}.
 */
@Mapper(componentModel = "spring")
public interface ClothMapper extends EntityMapper<ClothDTO, Cloth> {}
