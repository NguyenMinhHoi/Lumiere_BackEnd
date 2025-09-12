package com.lumi.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.lumi.app.domain.SurveyResponse;
import com.lumi.app.repository.SurveyResponseRepository;
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
 * Spring Data Elasticsearch repository for the {@link SurveyResponse} entity.
 */
public interface SurveyResponseSearchRepository
    extends ElasticsearchRepository<SurveyResponse, Long>, SurveyResponseSearchRepositoryInternal {}

interface SurveyResponseSearchRepositoryInternal {
    Page<SurveyResponse> search(String query, Pageable pageable);

    Page<SurveyResponse> search(Query query);

    @Async
    void index(SurveyResponse entity);

    @Async
    void deleteFromIndexById(Long id);
}

class SurveyResponseSearchRepositoryInternalImpl implements SurveyResponseSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final SurveyResponseRepository repository;

    SurveyResponseSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, SurveyResponseRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<SurveyResponse> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<SurveyResponse> search(Query query) {
        SearchHits<SurveyResponse> searchHits = elasticsearchTemplate.search(query, SurveyResponse.class);
        List<SurveyResponse> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(SurveyResponse entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), SurveyResponse.class);
    }
}
