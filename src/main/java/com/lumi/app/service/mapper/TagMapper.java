package com.lumi.app.service.mapper;

import com.lumi.app.domain.KnowledgeArticle;
import com.lumi.app.domain.Tag;
import com.lumi.app.domain.Ticket;
import com.lumi.app.service.dto.KnowledgeArticleDTO;
import com.lumi.app.service.dto.TagDTO;
import com.lumi.app.service.dto.TicketDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Tag} and its DTO {@link TagDTO}.
 */
@Mapper(componentModel = "spring")
public interface TagMapper extends EntityMapper<TagDTO, Tag> {
    @Mapping(target = "tickets", source = "tickets", qualifiedByName = "ticketIdSet")
    @Mapping(target = "articles", source = "articles", qualifiedByName = "knowledgeArticleIdSet")
    TagDTO toDto(Tag s);

    @Mapping(target = "tickets", ignore = true)
    @Mapping(target = "removeTickets", ignore = true)
    @Mapping(target = "articles", ignore = true)
    @Mapping(target = "removeArticles", ignore = true)
    Tag toEntity(TagDTO tagDTO);

    @Named("ticketId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TicketDTO toDtoTicketId(Ticket ticket);

    @Named("ticketIdSet")
    default Set<TicketDTO> toDtoTicketIdSet(Set<Ticket> ticket) {
        return ticket.stream().map(this::toDtoTicketId).collect(Collectors.toSet());
    }

    @Named("knowledgeArticleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    KnowledgeArticleDTO toDtoKnowledgeArticleId(KnowledgeArticle knowledgeArticle);

    @Named("knowledgeArticleIdSet")
    default Set<KnowledgeArticleDTO> toDtoKnowledgeArticleIdSet(Set<KnowledgeArticle> knowledgeArticle) {
        return knowledgeArticle.stream().map(this::toDtoKnowledgeArticleId).collect(Collectors.toSet());
    }
}
