package com.lumi.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.lumi.app.domain.VoucherRedemption;
import com.lumi.app.repository.VoucherRedemptionRepository;
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
 * Spring Data Elasticsearch repository for the {@link VoucherRedemption} entity.
 */
public interface VoucherRedemptionSearchRepository
    extends ElasticsearchRepository<VoucherRedemption, Long>, VoucherRedemptionSearchRepositoryInternal {}

interface VoucherRedemptionSearchRepositoryInternal {
    Page<VoucherRedemption> search(String query, Pageable pageable);

    Page<VoucherRedemption> search(Query query);

    @Async
    void index(VoucherRedemption entity);

    @Async
    void deleteFromIndexById(Long id);
}

class VoucherRedemptionSearchRepositoryInternalImpl implements VoucherRedemptionSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final VoucherRedemptionRepository repository;

    VoucherRedemptionSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, VoucherRedemptionRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<VoucherRedemption> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<VoucherRedemption> search(Query query) {
        SearchHits<VoucherRedemption> searchHits = elasticsearchTemplate.search(query, VoucherRedemption.class);
        List<VoucherRedemption> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(VoucherRedemption entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), VoucherRedemption.class);
    }
}
