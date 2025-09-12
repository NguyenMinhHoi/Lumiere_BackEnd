package com.lumi.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.lumi.app.domain.ProductVariant;
import com.lumi.app.repository.ProductVariantRepository;
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
 * Spring Data Elasticsearch repository for the {@link ProductVariant} entity.
 */
public interface ProductVariantSearchRepository
    extends ElasticsearchRepository<ProductVariant, Long>, ProductVariantSearchRepositoryInternal {}

interface ProductVariantSearchRepositoryInternal {
    Page<ProductVariant> search(String query, Pageable pageable);

    Page<ProductVariant> search(Query query);

    @Async
    void index(ProductVariant entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ProductVariantSearchRepositoryInternalImpl implements ProductVariantSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ProductVariantRepository repository;

    ProductVariantSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ProductVariantRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<ProductVariant> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<ProductVariant> search(Query query) {
        SearchHits<ProductVariant> searchHits = elasticsearchTemplate.search(query, ProductVariant.class);
        List<ProductVariant> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(ProductVariant entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), ProductVariant.class);
    }
}
