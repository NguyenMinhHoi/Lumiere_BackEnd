package com.lumi.app.service.dto;

import com.lumi.app.domain.enumeration.AppType;
import com.lumi.app.domain.enumeration.IntegrationStatus;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.app.domain.IntegrationLog} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IntegrationLogDTO implements Serializable {

    private Long id;

    @NotNull
    private AppType sourceApp;

    @NotNull
    private AppType targetApp;

    @Lob
    private String payload;

    @Lob
    private String response;

    @NotNull
    private IntegrationStatus status;

    @NotNull
    @Min(value = 0)
    private Integer retries;

    @NotNull
    private Instant createdAt;

    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AppType getSourceApp() {
        return sourceApp;
    }

    public void setSourceApp(AppType sourceApp) {
        this.sourceApp = sourceApp;
    }

    public AppType getTargetApp() {
        return targetApp;
    }

    public void setTargetApp(AppType targetApp) {
        this.targetApp = targetApp;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public IntegrationStatus getStatus() {
        return status;
    }

    public void setStatus(IntegrationStatus status) {
        this.status = status;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IntegrationLogDTO)) {
            return false;
        }

        IntegrationLogDTO integrationLogDTO = (IntegrationLogDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, integrationLogDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IntegrationLogDTO{" +
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
