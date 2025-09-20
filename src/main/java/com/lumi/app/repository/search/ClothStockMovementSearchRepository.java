package com.lumi.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.lumi.app.domain.ClothStockMovement;
import com.lumi.app.repository.ClothStockMovementRepository;
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
 * Spring Data Elasticsearch repository for the {@link ClothStockMovement} entity.
 */
public interface ClothStockMovementSearchRepository
    extends ElasticsearchRepository<ClothStockMovement, Long>, ClothStockMovementSearchRepositoryInternal {}

interface ClothStockMovementSearchRepositoryInternal {
    Page<ClothStockMovement> search(String query, Pageable pageable);

    Page<ClothStockMovement> search(Query query);

    @Async
    void index(ClothStockMovement entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ClothStockMovementSearchRepositoryInternalImpl implements ClothStockMovementSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ClothStockMovementRepository repository;

    ClothStockMovementSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ClothStockMovementRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<ClothStockMovement> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<ClothStockMovement> search(Query query) {
        SearchHits<ClothStockMovement> searchHits = elasticsearchTemplate.search(query, ClothStockMovement.class);
        List<ClothStockMovement> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(ClothStockMovement entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), ClothStockMovement.class);
    }
}
