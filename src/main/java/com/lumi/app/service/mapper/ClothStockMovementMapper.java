package com.lumi.app.service.mapper;

import com.lumi.app.domain.ClothStockMovement;
import com.lumi.app.service.dto.ClothStockMovementDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ClothStockMovement} and its DTO {@link ClothStockMovementDTO}.
 */
@Mapper(componentModel = "spring")
public interface ClothStockMovementMapper extends EntityMapper<ClothStockMovementDTO, ClothStockMovement> {}
