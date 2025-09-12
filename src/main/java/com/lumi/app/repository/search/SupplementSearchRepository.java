package com.lumi.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.lumi.app.domain.Supplement;
import com.lumi.app.repository.SupplementRepository;
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
 * Spring Data Elasticsearch repository for the {@link Supplement} entity.
 */
public interface SupplementSearchRepository extends ElasticsearchRepository<Supplement, Long>, SupplementSearchRepositoryInternal {}

interface SupplementSearchRepositoryInternal {
    Page<Supplement> search(String query, Pageable pageable);

    Page<Supplement> search(Query query);

    @Async
    void index(Supplement entity);

    @Async
    void deleteFromIndexById(Long id);
}

class SupplementSearchRepositoryInternalImpl implements SupplementSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final SupplementRepository repository;

    SupplementSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, SupplementRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Supplement> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Supplement> search(Query query) {
        SearchHits<Supplement> searchHits = elasticsearchTemplate.search(query, Supplement.class);
        List<Supplement> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Supplement entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Supplement.class);
    }
}
