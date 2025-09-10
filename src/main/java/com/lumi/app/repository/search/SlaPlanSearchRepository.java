package com.lumi.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.lumi.app.domain.SlaPlan;
import com.lumi.app.repository.SlaPlanRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link SlaPlan} entity.
 */
public interface SlaPlanSearchRepository extends ElasticsearchRepository<SlaPlan, Long>, SlaPlanSearchRepositoryInternal {}

interface SlaPlanSearchRepositoryInternal {
    Stream<SlaPlan> search(String query);

    Stream<SlaPlan> search(Query query);

    @Async
    void index(SlaPlan entity);

    @Async
    void deleteFromIndexById(Long id);
}

class SlaPlanSearchRepositoryInternalImpl implements SlaPlanSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final SlaPlanRepository repository;

    SlaPlanSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, SlaPlanRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<SlaPlan> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<SlaPlan> search(Query query) {
        return elasticsearchTemplate.search(query, SlaPlan.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(SlaPlan entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), SlaPlan.class);
    }
}
