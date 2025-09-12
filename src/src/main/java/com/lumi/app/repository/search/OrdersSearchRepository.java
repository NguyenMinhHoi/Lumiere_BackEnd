package com.lumi.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.lumi.app.domain.Orders;
import com.lumi.app.repository.OrdersRepository;
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
 * Spring Data Elasticsearch repository for the {@link Orders} entity.
 */
public interface OrdersSearchRepository extends ElasticsearchRepository<Orders, Long>, OrdersSearchRepositoryInternal {}

interface OrdersSearchRepositoryInternal {
    Page<Orders> search(String query, Pageable pageable);

    Page<Orders> search(Query query);

    @Async
    void index(Orders entity);

    @Async
    void deleteFromIndexById(Long id);
}

class OrdersSearchRepositoryInternalImpl implements OrdersSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final OrdersRepository repository;

    OrdersSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, OrdersRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Orders> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Orders> search(Query query) {
        SearchHits<Orders> searchHits = elasticsearchTemplate.search(query, Orders.class);
        List<Orders> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Orders entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Orders.class);
    }
}
