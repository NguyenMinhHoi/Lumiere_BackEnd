package com.lumi.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.lumi.app.domain.CompanyConfig;
import com.lumi.app.repository.CompanyConfigRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link CompanyConfig} entity.
 */
public interface CompanyConfigSearchRepository
    extends ElasticsearchRepository<CompanyConfig, Long>, CompanyConfigSearchRepositoryInternal {}

interface CompanyConfigSearchRepositoryInternal {
    Stream<CompanyConfig> search(String query);

    Stream<CompanyConfig> search(Query query);

    @Async
    void index(CompanyConfig entity);

    @Async
    void deleteFromIndexById(Long id);
}

class CompanyConfigSearchRepositoryInternalImpl implements CompanyConfigSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final CompanyConfigRepository repository;

    CompanyConfigSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, CompanyConfigRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<CompanyConfig> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<CompanyConfig> search(Query query) {
        return elasticsearchTemplate.search(query, CompanyConfig.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(CompanyConfig entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), CompanyConfig.class);
    }
}
