package com.lumi.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.lumi.app.domain.IntegrationLog;
import com.lumi.app.repository.IntegrationLogRepository;
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
 * Spring Data Elasticsearch repository for the {@link IntegrationLog} entity.
 */
public interface IntegrationLogSearchRepository
    extends ElasticsearchRepository<IntegrationLog, Long>, IntegrationLogSearchRepositoryInternal {}

interface IntegrationLogSearchRepositoryInternal {
    Page<IntegrationLog> search(String query, Pageable pageable);

    Page<IntegrationLog> search(Query query);

    @Async
    void index(IntegrationLog entity);

    @Async
    void deleteFromIndexById(Long id);
}

class IntegrationLogSearchRepositoryInternalImpl implements IntegrationLogSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final IntegrationLogRepository repository;

    IntegrationLogSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, IntegrationLogRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<IntegrationLog> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<IntegrationLog> search(Query query) {
        SearchHits<IntegrationLog> searchHits = elasticsearchTemplate.search(query, IntegrationLog.class);
        List<IntegrationLog> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(IntegrationLog entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), IntegrationLog.class);
    }
}
