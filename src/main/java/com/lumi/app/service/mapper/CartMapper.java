package com.lumi.app.service.mapper;

import com.lumi.app.domain.Cart;
import com.lumi.app.service.dto.CartDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Cart} and its DTO {@link CartDTO}.
 */
@Mapper(componentModel = "spring")
public interface CartMapper extends EntityMapper<CartDTO, Cart> {}
