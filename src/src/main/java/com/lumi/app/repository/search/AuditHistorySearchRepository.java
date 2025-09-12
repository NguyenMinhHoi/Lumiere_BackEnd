package com.lumi.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.lumi.app.domain.AuditHistory;
import com.lumi.app.repository.AuditHistoryRepository;
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
 * Spring Data Elasticsearch repository for the {@link AuditHistory} entity.
 */
public interface AuditHistorySearchRepository extends ElasticsearchRepository<AuditHistory, Long>, AuditHistorySearchRepositoryInternal {}

interface AuditHistorySearchRepositoryInternal {
    Page<AuditHistory> search(String query, Pageable pageable);

    Page<AuditHistory> search(Query query);

    @Async
    void index(AuditHistory entity);

    @Async
    void deleteFromIndexById(Long id);
}

class AuditHistorySearchRepositoryInternalImpl implements AuditHistorySearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final AuditHistoryRepository repository;

    AuditHistorySearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, AuditHistoryRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<AuditHistory> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<AuditHistory> search(Query query) {
        SearchHits<AuditHistory> searchHits = elasticsearchTemplate.search(query, AuditHistory.class);
        List<AuditHistory> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(AuditHistory entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), AuditHistory.class);
    }
}
