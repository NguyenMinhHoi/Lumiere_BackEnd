package com.lumi.app.service.mapper;

import com.lumi.app.domain.KnowledgeArticle;
import com.lumi.app.domain.KnowledgeCategory;
import com.lumi.app.domain.Tag;
import com.lumi.app.service.dto.KnowledgeArticleDTO;
import com.lumi.app.service.dto.KnowledgeCategoryDTO;
import com.lumi.app.service.dto.TagDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link KnowledgeArticle} and its DTO {@link KnowledgeArticleDTO}.
 */
@Mapper(componentModel = "spring")
public interface KnowledgeArticleMapper extends EntityMapper<KnowledgeArticleDTO, KnowledgeArticle> {
    @Mapping(target = "category", source = "category", qualifiedByName = "knowledgeCategoryName")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "tagNameSet")
    KnowledgeArticleDTO toDto(KnowledgeArticle s);

    @Mapping(target = "removeTags", ignore = true)
    KnowledgeArticle toEntity(KnowledgeArticleDTO knowledgeArticleDTO);

    @Named("knowledgeCategoryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    KnowledgeCategoryDTO toDtoKnowledgeCategoryName(KnowledgeCategory knowledgeCategory);

    @Named("tagName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TagDTO toDtoTagName(Tag tag);

    @Named("tagNameSet")
    default Set<TagDTO> toDtoTagNameSet(Set<Tag> tag) {
        return tag.stream().map(this::toDtoTagName).collect(Collectors.toSet());
    }
}
