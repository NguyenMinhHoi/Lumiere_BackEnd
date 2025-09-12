package com.lumi.app.service.mapper;

import com.lumi.app.domain.Campaign;
import com.lumi.app.service.dto.CampaignDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Campaign} and its DTO {@link CampaignDTO}.
 */
@Mapper(componentModel = "spring")
public interface CampaignMapper extends EntityMapper<CampaignDTO, Campaign> {}
