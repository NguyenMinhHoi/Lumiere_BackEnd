package com.lumi.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.lumi.app.domain.Cart;
import com.lumi.app.repository.CartRepository;
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
 * Spring Data Elasticsearch repository for the {@link Cart} entity.
 */
public interface CartSearchRepository extends ElasticsearchRepository<Cart, Long>, CartSearchRepositoryInternal {}

interface CartSearchRepositoryInternal {
    Page<Cart> search(String query, Pageable pageable);

    Page<Cart> search(Query query);

    @Async
    void index(Cart entity);

    @Async
    void deleteFromIndexById(Long id);
}

class CartSearchRepositoryInternalImpl implements CartSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final CartRepository repository;

    CartSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, CartRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Cart> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Cart> search(Query query) {
        SearchHits<Cart> searchHits = elasticsearchTemplate.search(query, Cart.class);
        List<Cart> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Cart entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Cart.class);
    }
}
