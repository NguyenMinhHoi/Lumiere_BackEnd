package com.lumi.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.lumi.app.domain.Survey;
import com.lumi.app.repository.SurveyRepository;
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
 * Spring Data Elasticsearch repository for the {@link Survey} entity.
 */
public interface SurveySearchRepository extends ElasticsearchRepository<Survey, Long>, SurveySearchRepositoryInternal {}

interface SurveySearchRepositoryInternal {
    Page<Survey> search(String query, Pageable pageable);

    Page<Survey> search(Query query);

    @Async
    void index(Survey entity);

    @Async
    void deleteFromIndexById(Long id);
}

class SurveySearchRepositoryInternalImpl implements SurveySearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final SurveyRepository repository;

    SurveySearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, SurveyRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Survey> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Survey> search(Query query) {
        SearchHits<Survey> searchHits = elasticsearchTemplate.search(query, Survey.class);
        List<Survey> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Survey entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Survey.class);
    }
}
