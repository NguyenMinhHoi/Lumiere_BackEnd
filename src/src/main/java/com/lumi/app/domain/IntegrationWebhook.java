package com.lumi.app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A IntegrationWebhook.
 */
@Entity
@Table(name = "integration_webhook")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "integrationwebhook")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IntegrationWebhook implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 128)
    @Column(name = "name", length = 128, nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @NotNull
    @Size(max = 512)
    @Column(name = "target_url", length = 512, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String targetUrl;

    @Size(max = 128)
    @Column(name = "secret", length = 128)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String secret;

    @NotNull
    @Column(name = "is_active", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean isActive;

    @Size(max = 512)
    @Column(name = "subscribed_events", length = 512)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String subscribedEvents;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public IntegrationWebhook id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public IntegrationWebhook name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTargetUrl() {
        return this.targetUrl;
    }

    public IntegrationWebhook targetUrl(String targetUrl) {
        this.setTargetUrl(targetUrl);
        return this;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getSecret() {
        return this.secret;
    }

    public IntegrationWebhook secret(String secret) {
        this.setSecret(secret);
        return this;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public IntegrationWebhook isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getSubscribedEvents() {
        return this.subscribedEvents;
    }

    public IntegrationWebhook subscribedEvents(String subscribedEvents) {
        this.setSubscribedEvents(subscribedEvents);
        return this;
    }

    public void setSubscribedEvents(String subscribedEvents) {
        this.subscribedEvents = subscribedEvents;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IntegrationWebhook)) {
            return false;
        }
        return getId() != null && getId().equals(((IntegrationWebhook) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IntegrationWebhook{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", targetUrl='" + getTargetUrl() + "'" +
            ", secret='" + getSecret() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", subscribedEvents='" + getSubscribedEvents() + "'" +
            "}";
    }
}
