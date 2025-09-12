package com.lumi.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.lumi.app.domain.KnowledgeArticle;
import com.lumi.app.repository.KnowledgeArticleRepository;
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
 * Spring Data Elasticsearch repository for the {@link KnowledgeArticle} entity.
 */
public interface KnowledgeArticleSearchRepository
    extends ElasticsearchRepository<KnowledgeArticle, Long>, KnowledgeArticleSearchRepositoryInternal {}

interface KnowledgeArticleSearchRepositoryInternal {
    Page<KnowledgeArticle> search(String query, Pageable pageable);

    Page<KnowledgeArticle> search(Query query);

    @Async
    void index(KnowledgeArticle entity);

    @Async
    void deleteFromIndexById(Long id);
}

class KnowledgeArticleSearchRepositoryInternalImpl implements KnowledgeArticleSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final KnowledgeArticleRepository repository;

    KnowledgeArticleSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, KnowledgeArticleRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<KnowledgeArticle> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<KnowledgeArticle> search(Query query) {
        SearchHits<KnowledgeArticle> searchHits = elasticsearchTemplate.search(query, KnowledgeArticle.class);
        List<KnowledgeArticle> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(KnowledgeArticle entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), KnowledgeArticle.class);
    }
}
