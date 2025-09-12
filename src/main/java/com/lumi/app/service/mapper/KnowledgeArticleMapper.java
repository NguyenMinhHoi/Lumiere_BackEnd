package com.lumi.app.service.mapper;

import com.lumi.app.domain.KnowledgeArticle;
import com.lumi.app.service.dto.KnowledgeArticleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link KnowledgeArticle} and its DTO {@link KnowledgeArticleDTO}.
 */
@Mapper(componentModel = "spring")
public interface KnowledgeArticleMapper extends EntityMapper<KnowledgeArticleDTO, KnowledgeArticle> {}
