package com.lumi.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.lumi.app.domain.KnowledgeCategory;
import com.lumi.app.repository.KnowledgeCategoryRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link KnowledgeCategory} entity.
 */
public interface KnowledgeCategorySearchRepository
    extends ElasticsearchRepository<KnowledgeCategory, Long>, KnowledgeCategorySearchRepositoryInternal {}

interface KnowledgeCategorySearchRepositoryInternal {
    Stream<KnowledgeCategory> search(String query);

    Stream<KnowledgeCategory> search(Query query);

    @Async
    void index(KnowledgeCategory entity);

    @Async
    void deleteFromIndexById(Long id);
}

class KnowledgeCategorySearchRepositoryInternalImpl implements KnowledgeCategorySearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final KnowledgeCategoryRepository repository;

    KnowledgeCategorySearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, KnowledgeCategoryRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<KnowledgeCategory> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<KnowledgeCategory> search(Query query) {
        return elasticsearchTemplate.search(query, KnowledgeCategory.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(KnowledgeCategory entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), KnowledgeCategory.class);
    }
}
