package com.lumi.app.service.mapper;

import com.lumi.app.domain.ClothInventory;
import com.lumi.app.service.dto.ClothInventoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ClothInventory} and its DTO {@link ClothInventoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface ClothInventoryMapper extends EntityMapper<ClothInventoryDTO, ClothInventory> {}
