package com.lumi.app.service.mapper;

import com.lumi.app.domain.Attachment;
import com.lumi.app.service.dto.AttachmentDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Attachment} and its DTO {@link AttachmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface AttachmentMapper extends EntityMapper<AttachmentDTO, Attachment> {}
