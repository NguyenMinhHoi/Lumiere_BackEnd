package com.lumi.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.lumi.app.domain.Inventory;
import com.lumi.app.repository.InventoryRepository;
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
 * Spring Data Elasticsearch repository for the {@link Inventory} entity.
 */
public interface InventorySearchRepository extends ElasticsearchRepository<Inventory, Long>, InventorySearchRepositoryInternal {}

interface InventorySearchRepositoryInternal {
    Page<Inventory> search(String query, Pageable pageable);

    Page<Inventory> search(Query query);

    @Async
    void index(Inventory entity);

    @Async
    void deleteFromIndexById(Long id);
}

class InventorySearchRepositoryInternalImpl implements InventorySearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final InventoryRepository repository;

    InventorySearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, InventoryRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Inventory> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Inventory> search(Query query) {
        SearchHits<Inventory> searchHits = elasticsearchTemplate.search(query, Inventory.class);
        List<Inventory> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Inventory entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Inventory.class);
    }
}
