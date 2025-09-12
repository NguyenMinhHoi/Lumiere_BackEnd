package com.lumi.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.lumi.app.domain.TicketFile;
import com.lumi.app.repository.TicketFileRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link TicketFile} entity.
 */
public interface TicketFileSearchRepository extends ElasticsearchRepository<TicketFile, Long>, TicketFileSearchRepositoryInternal {}

interface TicketFileSearchRepositoryInternal {
    Page<TicketFile> search(String query, Pageable pageable);

    Page<TicketFile> search(Query query);

    @Async
    void index(TicketFile entity);

    @Async
    void deleteFromIndexById(Long id);
}

class TicketFileSearchRepositoryInternalImpl implements TicketFileSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final TicketFileRepository repository;

    TicketFileSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, TicketFileRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<TicketFile> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<TicketFile> search(Query query) {
        SearchHits<TicketFile> searchHits = elasticsearchTemplate.search(query, TicketFile.class);
        List<TicketFile> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(TicketFile entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), TicketFile.class);
    }
}
