package com.lumi.app.service.mapper;

import com.lumi.app.domain.Tag;
import com.lumi.app.service.dto.TagDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Tag} and its DTO {@link TagDTO}.
 */
@Mapper(componentModel = "spring")
public interface TagMapper extends EntityMapper<TagDTO, Tag> {}
