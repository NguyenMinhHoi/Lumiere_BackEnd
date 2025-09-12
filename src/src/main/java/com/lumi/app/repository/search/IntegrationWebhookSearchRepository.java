package com.lumi.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.lumi.app.domain.IntegrationWebhook;
import com.lumi.app.repository.IntegrationWebhookRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link IntegrationWebhook} entity.
 */
public interface IntegrationWebhookSearchRepository
    extends ElasticsearchRepository<IntegrationWebhook, Long>, IntegrationWebhookSearchRepositoryInternal {}

interface IntegrationWebhookSearchRepositoryInternal {
    Stream<IntegrationWebhook> search(String query);

    Stream<IntegrationWebhook> search(Query query);

    @Async
    void index(IntegrationWebhook entity);

    @Async
    void deleteFromIndexById(Long id);
}

class IntegrationWebhookSearchRepositoryInternalImpl implements IntegrationWebhookSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final IntegrationWebhookRepository repository;

    IntegrationWebhookSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, IntegrationWebhookRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<IntegrationWebhook> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<IntegrationWebhook> search(Query query) {
        return elasticsearchTemplate.search(query, IntegrationWebhook.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(IntegrationWebhook entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), IntegrationWebhook.class);
    }
}
