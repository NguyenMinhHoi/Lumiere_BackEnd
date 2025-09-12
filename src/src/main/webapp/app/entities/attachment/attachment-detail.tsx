import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './attachment.reducer';

export const AttachmentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const attachmentEntity = useAppSelector(state => state.attachment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="attachmentDetailsHeading">
          <Translate contentKey="lumiApp.attachment.detail.title">Attachment</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{attachmentEntity.id}</dd>
          <dt>
            <span id="ticketId">
              <Translate contentKey="lumiApp.attachment.ticketId">Ticket Id</Translate>
            </span>
          </dt>
          <dd>{attachmentEntity.ticketId}</dd>
          <dt>
            <span id="commentId">
              <Translate contentKey="lumiApp.attachment.commentId">Comment Id</Translate>
            </span>
          </dt>
          <dd>{attachmentEntity.commentId}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="lumiApp.attachment.name">Name</Translate>
            </span>
          </dt>
          <dd>{attachmentEntity.name}</dd>
          <dt>
            <span id="url">
              <Translate contentKey="lumiApp.attachment.url">Url</Translate>
            </span>
          </dt>
          <dd>{attachmentEntity.url}</dd>
          <dt>
            <span id="contentType">
              <Translate contentKey="lumiApp.attachment.contentType">Content Type</Translate>
            </span>
          </dt>
          <dd>{attachmentEntity.contentType}</dd>
          <dt>
            <span id="size">
              <Translate contentKey="lumiApp.attachment.size">Size</Translate>
            </span>
          </dt>
          <dd>{attachmentEntity.size}</dd>
          <dt>
            <span id="uploadedAt">
              <Translate contentKey="lumiApp.attachment.uploadedAt">Uploaded At</Translate>
            </span>
          </dt>
          <dd>
            {attachmentEntity.uploadedAt ? <TextFormat value={attachmentEntity.uploadedAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
        </dl>
        <Button tag={Link} to="/attachment" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/attachment/${attachmentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AttachmentDetail;
