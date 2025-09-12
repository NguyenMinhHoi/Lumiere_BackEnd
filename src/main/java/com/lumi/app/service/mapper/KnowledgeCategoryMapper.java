package com.lumi.app.service.mapper;

import com.lumi.app.domain.KnowledgeCategory;
import com.lumi.app.service.dto.KnowledgeCategoryDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link KnowledgeCategory} and its DTO {@link KnowledgeCategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface KnowledgeCategoryMapper extends EntityMapper<KnowledgeCategoryDTO, KnowledgeCategory> {}
