package com.lumi.app.service.mapper;

import com.lumi.app.domain.CartItem;
import com.lumi.app.service.dto.CartItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CartItem} and its DTO {@link CartItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface CartItemMapper extends EntityMapper<CartItemDTO, CartItem> {}
