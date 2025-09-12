package com.lumi.app.domain;

import com.lumi.app.domain.enumeration.AppType;
import com.lumi.app.domain.enumeration.IntegrationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A IntegrationLog.
 */
@Entity
@Table(name = "integration_log")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "integrationlog")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IntegrationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "source_app", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private AppType sourceApp;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "target_app", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private AppType targetApp;

    @Lob
    @Column(name = "payload", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String payload;

    @Lob
    @Column(name = "response")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String response;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private IntegrationStatus status;

    @NotNull
    @Min(value = 0)
    @Column(name = "retries", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer retries;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public IntegrationLog id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AppType getSourceApp() {
        return this.sourceApp;
    }

    public IntegrationLog sourceApp(AppType sourceApp) {
        this.setSourceApp(sourceApp);
        return this;
    }

    public void setSourceApp(AppType sourceApp) {
        this.sourceApp = sourceApp;
    }

    public AppType getTargetApp() {
        return this.targetApp;
    }

    public IntegrationLog targetApp(AppType targetApp) {
        this.setTargetApp(targetApp);
        return this;
    }

    public void setTargetApp(AppType targetApp) {
        this.targetApp = targetApp;
    }

    public String getPayload() {
        return this.payload;
    }

    public IntegrationLog payload(String payload) {
        this.setPayload(payload);
        return this;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getResponse() {
        return this.response;
    }

    public IntegrationLog response(String response) {
        this.setResponse(response);
        return this;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public IntegrationStatus getStatus() {
        return this.status;
    }

    public IntegrationLog status(IntegrationStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(IntegrationStatus status) {
        this.status = status;
    }

    public Integer getRetries() {
        return this.retries;
    }

    public IntegrationLog retries(Integer retries) {
        this.setRetries(retries);
        return this;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public IntegrationLog createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public IntegrationLog updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IntegrationLog)) {
            return false;
        }
        return getId() != null && getId().equals(((IntegrationLog) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IntegrationLog{" +
            "id=" + getId() +
            ", sourceApp='" + getSourceApp() + "'" +
            ", targetApp='" + getTargetApp() + "'" +
            ", payload='" + getPayload() + "'" +
            ", response='" + getResponse() + "'" +
            ", status='" + getStatus() + "'" +
            ", retries=" + getRetries() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
