package com.lumi.app.service.mapper;

import com.lumi.app.domain.StockMovement;
import com.lumi.app.service.dto.StockMovementDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link StockMovement} and its DTO {@link StockMovementDTO}.
 */
@Mapper(componentModel = "spring")
public interface StockMovementMapper extends EntityMapper<StockMovementDTO, StockMovement> {}
