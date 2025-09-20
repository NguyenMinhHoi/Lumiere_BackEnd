package com.lumi.app;

import com.lumi.app.config.AsyncSyncConfiguration;
import com.lumi.app.config.EmbeddedElasticsearch;
import com.lumi.app.config.EmbeddedKafka;
import com.lumi.app.config.EmbeddedSQL;
import com.lumi.app.config.JacksonConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { LumiApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class })
@EmbeddedElasticsearch
@EmbeddedSQL
@EmbeddedKafka
public @interface IntegrationTest {
}
