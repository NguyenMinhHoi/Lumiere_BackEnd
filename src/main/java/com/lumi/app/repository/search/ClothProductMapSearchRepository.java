package com.lumi.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.lumi.app.domain.ClothProductMap;
import com.lumi.app.repository.ClothProductMapRepository;
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
 * Spring Data Elasticsearch repository for the {@link ClothProductMap} entity.
 */
public interface ClothProductMapSearchRepository
    extends ElasticsearchRepository<ClothProductMap, Long>, ClothProductMapSearchRepositoryInternal {}

interface ClothProductMapSearchRepositoryInternal {
    Page<ClothProductMap> search(String query, Pageable pageable);

    Page<ClothProductMap> search(Query query);

    @Async
    void index(ClothProductMap entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ClothProductMapSearchRepositoryInternalImpl implements ClothProductMapSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ClothProductMapRepository repository;

    ClothProductMapSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ClothProductMapRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<ClothProductMap> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<ClothProductMap> search(Query query) {
        SearchHits<ClothProductMap> searchHits = elasticsearchTemplate.search(query, ClothProductMap.class);
        List<ClothProductMap> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(ClothProductMap entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), ClothProductMap.class);
    }
}
