package com.lumi.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.lumi.app.domain.ClothInventory;
import com.lumi.app.repository.ClothInventoryRepository;
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
 * Spring Data Elasticsearch repository for the {@link ClothInventory} entity.
 */
public interface ClothInventorySearchRepository
    extends ElasticsearchRepository<ClothInventory, Long>, ClothInventorySearchRepositoryInternal {}

interface ClothInventorySearchRepositoryInternal {
    Page<ClothInventory> search(String query, Pageable pageable);

    Page<ClothInventory> search(Query query);

    @Async
    void index(ClothInventory entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ClothInventorySearchRepositoryInternalImpl implements ClothInventorySearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ClothInventoryRepository repository;

    ClothInventorySearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ClothInventoryRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<ClothInventory> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<ClothInventory> search(Query query) {
        SearchHits<ClothInventory> searchHits = elasticsearchTemplate.search(query, ClothInventory.class);
        List<ClothInventory> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(ClothInventory entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), ClothInventory.class);
    }
}
