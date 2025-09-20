package com.lumi.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.lumi.app.domain.ClothSupplement;
import com.lumi.app.repository.ClothSupplementRepository;
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
 * Spring Data Elasticsearch repository for the {@link ClothSupplement} entity.
 */
public interface ClothSupplementSearchRepository
    extends ElasticsearchRepository<ClothSupplement, Long>, ClothSupplementSearchRepositoryInternal {}

interface ClothSupplementSearchRepositoryInternal {
    Page<ClothSupplement> search(String query, Pageable pageable);

    Page<ClothSupplement> search(Query query);

    @Async
    void index(ClothSupplement entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ClothSupplementSearchRepositoryInternalImpl implements ClothSupplementSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ClothSupplementRepository repository;

    ClothSupplementSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ClothSupplementRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<ClothSupplement> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<ClothSupplement> search(Query query) {
        SearchHits<ClothSupplement> searchHits = elasticsearchTemplate.search(query, ClothSupplement.class);
        List<ClothSupplement> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(ClothSupplement entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), ClothSupplement.class);
    }
}
