package com.lumi.app.service.impl;

import com.lumi.app.domain.Campaign;
import com.lumi.app.repository.CampaignRepository;
import com.lumi.app.repository.search.CampaignSearchRepository;
import com.lumi.app.service.CampaignService;
import com.lumi.app.service.dto.CampaignDTO;
import com.lumi.app.service.mapper.CampaignMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.Campaign}.
 */
@Service
@Transactional
public class CampaignServiceImpl implements CampaignService {

    private static final Logger LOG = LoggerFactory.getLogger(CampaignServiceImpl.class);

    private final CampaignRepository campaignRepository;

    private final CampaignMapper campaignMapper;

    private final CampaignSearchRepository campaignSearchRepository;

    public CampaignServiceImpl(
        CampaignRepository campaignRepository,
        CampaignMapper campaignMapper,
        CampaignSearchRepository campaignSearchRepository
    ) {
        this.campaignRepository = campaignRepository;
        this.campaignMapper = campaignMapper;
        this.campaignSearchRepository = campaignSearchRepository;
    }

    @Override
    public CampaignDTO save(CampaignDTO campaignDTO) {
        LOG.debug("Request to save Campaign : {}", campaignDTO);
        Campaign campaign = campaignMapper.toEntity(campaignDTO);
        campaign = campaignRepository.save(campaign);
        campaignSearchRepository.index(campaign);
        return campaignMapper.toDto(campaign);
    }

    @Override
    public CampaignDTO update(CampaignDTO campaignDTO) {
        LOG.debug("Request to update Campaign : {}", campaignDTO);
        Campaign campaign = campaignMapper.toEntity(campaignDTO);
        campaign = campaignRepository.save(campaign);
        campaignSearchRepository.index(campaign);
        return campaignMapper.toDto(campaign);
    }

    @Override
    public Optional<CampaignDTO> partialUpdate(CampaignDTO campaignDTO) {
        LOG.debug("Request to partially update Campaign : {}", campaignDTO);

        return campaignRepository
            .findById(campaignDTO.getId())
            .map(existingCampaign -> {
                campaignMapper.partialUpdate(existingCampaign, campaignDTO);

                return existingCampaign;
            })
            .map(campaignRepository::save)
            .map(savedCampaign -> {
                campaignSearchRepository.index(savedCampaign);
                return savedCampaign;
            })
            .map(campaignMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CampaignDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Campaigns");
        return campaignRepository.findAll(pageable).map(campaignMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CampaignDTO> findOne(Long id) {
        LOG.debug("Request to get Campaign : {}", id);
        return campaignRepository.findById(id).map(campaignMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Campaign : {}", id);
        campaignRepository.deleteById(id);
        campaignSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CampaignDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Campaigns for query {}", query);
        return campaignSearchRepository.search(query, pageable).map(campaignMapper::toDto);
    }
}
