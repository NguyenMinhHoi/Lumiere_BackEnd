package com.lumi.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.lumi.app.domain.Voucher;
import com.lumi.app.repository.VoucherRepository;
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
 * Spring Data Elasticsearch repository for the {@link Voucher} entity.
 */
public interface VoucherSearchRepository extends ElasticsearchRepository<Voucher, Long>, VoucherSearchRepositoryInternal {}

interface VoucherSearchRepositoryInternal {
    Page<Voucher> search(String query, Pageable pageable);

    Page<Voucher> search(Query query);

    @Async
    void index(Voucher entity);

    @Async
    void deleteFromIndexById(Long id);
}

class VoucherSearchRepositoryInternalImpl implements VoucherSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final VoucherRepository repository;

    VoucherSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, VoucherRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Voucher> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Voucher> search(Query query) {
        SearchHits<Voucher> searchHits = elasticsearchTemplate.search(query, Voucher.class);
        List<Voucher> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Voucher entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Voucher.class);
    }
}
