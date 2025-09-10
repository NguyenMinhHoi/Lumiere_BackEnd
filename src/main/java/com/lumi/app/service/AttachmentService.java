package com.lumi.app.service;

import com.lumi.app.domain.Attachment;
import com.lumi.app.repository.AttachmentRepository;
import com.lumi.app.repository.search.AttachmentSearchRepository;
import com.lumi.app.service.dto.AttachmentDTO;
import com.lumi.app.service.mapper.AttachmentMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.Attachment}.
 */
@Service
@Transactional
public class AttachmentService {

    private static final Logger LOG = LoggerFactory.getLogger(AttachmentService.class);

    private final AttachmentRepository attachmentRepository;

    private final AttachmentMapper attachmentMapper;

    private final AttachmentSearchRepository attachmentSearchRepository;

    public AttachmentService(
        AttachmentRepository attachmentRepository,
        AttachmentMapper attachmentMapper,
        AttachmentSearchRepository attachmentSearchRepository
    ) {
        this.attachmentRepository = attachmentRepository;
        this.attachmentMapper = attachmentMapper;
        this.attachmentSearchRepository = attachmentSearchRepository;
    }

    /**
     * Save a attachment.
     *
     * @param attachmentDTO the entity to save.
     * @return the persisted entity.
     */
    public AttachmentDTO save(AttachmentDTO attachmentDTO) {
        LOG.debug("Request to save Attachment : {}", attachmentDTO);
        Attachment attachment = attachmentMapper.toEntity(attachmentDTO);
        attachment = attachmentRepository.save(attachment);
        attachmentSearchRepository.index(attachment);
        return attachmentMapper.toDto(attachment);
    }

    /**
     * Update a attachment.
     *
     * @param attachmentDTO the entity to save.
     * @return the persisted entity.
     */
    public AttachmentDTO update(AttachmentDTO attachmentDTO) {
        LOG.debug("Request to update Attachment : {}", attachmentDTO);
        Attachment attachment = attachmentMapper.toEntity(attachmentDTO);
        attachment = attachmentRepository.save(attachment);
        attachmentSearchRepository.index(attachment);
        return attachmentMapper.toDto(attachment);
    }

    /**
     * Partially update a attachment.
     *
     * @param attachmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AttachmentDTO> partialUpdate(AttachmentDTO attachmentDTO) {
        LOG.debug("Request to partially update Attachment : {}", attachmentDTO);

        return attachmentRepository
            .findById(attachmentDTO.getId())
            .map(existingAttachment -> {
                attachmentMapper.partialUpdate(existingAttachment, attachmentDTO);

                return existingAttachment;
            })
            .map(attachmentRepository::save)
            .map(savedAttachment -> {
                attachmentSearchRepository.index(savedAttachment);
                return savedAttachment;
            })
            .map(attachmentMapper::toDto);
    }

    /**
     * Get all the attachments.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AttachmentDTO> findAll() {
        LOG.debug("Request to get all Attachments");
        return attachmentRepository.findAll().stream().map(attachmentMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the attachments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<AttachmentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return attachmentRepository.findAllWithEagerRelationships(pageable).map(attachmentMapper::toDto);
    }

    /**
     * Get one attachment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AttachmentDTO> findOne(Long id) {
        LOG.debug("Request to get Attachment : {}", id);
        return attachmentRepository.findOneWithEagerRelationships(id).map(attachmentMapper::toDto);
    }

    /**
     * Delete the attachment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Attachment : {}", id);
        attachmentRepository.deleteById(id);
        attachmentSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the attachment corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AttachmentDTO> search(String query) {
        LOG.debug("Request to search Attachments for query {}", query);
        try {
            return StreamSupport.stream(attachmentSearchRepository.search(query).spliterator(), false)
                .map(attachmentMapper::toDto)
                .toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
