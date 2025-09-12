package com.lumi.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.lumi.app.domain.CartItem;
import com.lumi.app.repository.CartItemRepository;
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
 * Spring Data Elasticsearch repository for the {@link CartItem} entity.
 */
public interface CartItemSearchRepository extends ElasticsearchRepository<CartItem, Long>, CartItemSearchRepositoryInternal {}

interface CartItemSearchRepositoryInternal {
    Page<CartItem> search(String query, Pageable pageable);

    Page<CartItem> search(Query query);

    @Async
    void index(CartItem entity);

    @Async
    void deleteFromIndexById(Long id);
}

class CartItemSearchRepositoryInternalImpl implements CartItemSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final CartItemRepository repository;

    CartItemSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, CartItemRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<CartItem> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<CartItem> search(Query query) {
        SearchHits<CartItem> searchHits = elasticsearchTemplate.search(query, CartItem.class);
        List<CartItem> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(CartItem entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), CartItem.class);
    }
}
