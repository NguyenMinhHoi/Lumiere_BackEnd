package com.lumi.app.config;

import com.github.benmanes.caffeine.jcache.configuration.CaffeineConfiguration;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.*;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.cache.PrefixedKeyGenerator;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private GitProperties gitProperties;
    private BuildProperties buildProperties;
    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Caffeine caffeine = jHipsterProperties.getCache().getCaffeine();

        CaffeineConfiguration<Object, Object> caffeineConfiguration = new CaffeineConfiguration<>();
        caffeineConfiguration.setMaximumSize(OptionalLong.of(caffeine.getMaxEntries()));
        caffeineConfiguration.setExpireAfterWrite(OptionalLong.of(TimeUnit.SECONDS.toNanos(caffeine.getTimeToLiveSeconds())));
        caffeineConfiguration.setStatisticsEnabled(true);
        jcacheConfiguration = caffeineConfiguration;
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, com.lumi.app.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, com.lumi.app.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, com.lumi.app.domain.User.class.getName());
            createCache(cm, com.lumi.app.domain.Authority.class.getName());
            createCache(cm, com.lumi.app.domain.User.class.getName() + ".authorities");
            createCache(cm, com.lumi.app.domain.Customer.class.getName());
            createCache(cm, com.lumi.app.domain.Ticket.class.getName());
            createCache(cm, com.lumi.app.domain.Ticket.class.getName() + ".tags");
            createCache(cm, com.lumi.app.domain.TicketComment.class.getName());
            createCache(cm, com.lumi.app.domain.Attachment.class.getName());
            createCache(cm, com.lumi.app.domain.ChannelMessage.class.getName());
            createCache(cm, com.lumi.app.domain.KnowledgeCategory.class.getName());
            createCache(cm, com.lumi.app.domain.KnowledgeArticle.class.getName());
            createCache(cm, com.lumi.app.domain.KnowledgeArticle.class.getName() + ".tags");
            createCache(cm, com.lumi.app.domain.Tag.class.getName());
            createCache(cm, com.lumi.app.domain.Tag.class.getName() + ".tickets");
            createCache(cm, com.lumi.app.domain.Tag.class.getName() + ".articles");
            createCache(cm, com.lumi.app.domain.SlaPlan.class.getName());
            createCache(cm, com.lumi.app.domain.Survey.class.getName());
            createCache(cm, com.lumi.app.domain.SurveyQuestion.class.getName());
            createCache(cm, com.lumi.app.domain.SurveyResponse.class.getName());
            createCache(cm, com.lumi.app.domain.Notification.class.getName());
            createCache(cm, com.lumi.app.domain.IntegrationWebhook.class.getName());
            createCache(cm, com.lumi.app.domain.TicketFile.class.getName());
            createCache(cm, com.lumi.app.domain.Product.class.getName());
            createCache(cm, com.lumi.app.domain.ProductVariant.class.getName());
            createCache(cm, com.lumi.app.domain.Orders.class.getName());
            createCache(cm, com.lumi.app.domain.OrderItem.class.getName());
            createCache(cm, com.lumi.app.domain.Supplier.class.getName());
            createCache(cm, com.lumi.app.domain.Supplement.class.getName());
            createCache(cm, com.lumi.app.domain.AuditHistory.class.getName());
            // jhipster-needle-caffeine-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
    }
}
