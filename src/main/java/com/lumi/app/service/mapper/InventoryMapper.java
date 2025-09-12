package com.lumi.app.service.mapper;

import com.lumi.app.domain.Inventory;
import com.lumi.app.service.dto.InventoryDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Inventory} and its DTO {@link InventoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface InventoryMapper extends EntityMapper<InventoryDTO, Inventory> {}
