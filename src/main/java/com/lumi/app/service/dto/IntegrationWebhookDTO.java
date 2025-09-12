package com.lumi.app.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.app.domain.IntegrationWebhook} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IntegrationWebhookDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 3, max = 128)
    private String name;

    @NotNull
    @Size(max = 512)
    private String targetUrl;

    @Size(max = 128)
    private String secret;

    @NotNull
    private Boolean isActive;

    @Size(max = 512)
    private String subscribedEvents;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getSubscribedEvents() {
        return subscribedEvents;
    }

    public void setSubscribedEvents(String subscribedEvents) {
        this.subscribedEvents = subscribedEvents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IntegrationWebhookDTO)) {
            return false;
        }

        IntegrationWebhookDTO integrationWebhookDTO = (IntegrationWebhookDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, integrationWebhookDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IntegrationWebhookDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", targetUrl='" + getTargetUrl() + "'" +
            ", secret='" + getSecret() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", subscribedEvents='" + getSubscribedEvents() + "'" +
            "}";
    }
}
