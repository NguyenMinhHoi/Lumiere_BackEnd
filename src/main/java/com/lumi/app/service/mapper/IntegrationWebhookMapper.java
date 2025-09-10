package com.lumi.app.service.mapper;

import com.lumi.app.domain.IntegrationWebhook;
import com.lumi.app.service.dto.IntegrationWebhookDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link IntegrationWebhook} and its DTO {@link IntegrationWebhookDTO}.
 */
@Mapper(componentModel = "spring")
public interface IntegrationWebhookMapper extends EntityMapper<IntegrationWebhookDTO, IntegrationWebhook> {}
