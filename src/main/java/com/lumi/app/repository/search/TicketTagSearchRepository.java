package com.lumi.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.lumi.app.domain.TicketTag;
import com.lumi.app.repository.TicketTagRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link TicketTag} entity.
 */
public interface TicketTagSearchRepository extends ElasticsearchRepository<TicketTag, Long>, TicketTagSearchRepositoryInternal {}

interface TicketTagSearchRepositoryInternal {
    Stream<TicketTag> search(String query);

    Stream<TicketTag> search(Query query);

    @Async
    void index(TicketTag entity);

    @Async
    void deleteFromIndexById(Long id);
}

class TicketTagSearchRepositoryInternalImpl implements TicketTagSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final TicketTagRepository repository;

    TicketTagSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, TicketTagRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<TicketTag> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<TicketTag> search(Query query) {
        return elasticsearchTemplate.search(query, TicketTag.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(TicketTag entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), TicketTag.class);
    }
}
