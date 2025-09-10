import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './ticket-file.reducer';

export const TicketFileDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const ticketFileEntity = useAppSelector(state => state.ticketFile.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="ticketFileDetailsHeading">
          <Translate contentKey="lumiApp.ticketFile.detail.title">TicketFile</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{ticketFileEntity.id}</dd>
          <dt>
            <span id="fileName">
              <Translate contentKey="lumiApp.ticketFile.fileName">File Name</Translate>
            </span>
          </dt>
          <dd>{ticketFileEntity.fileName}</dd>
          <dt>
            <span id="originalName">
              <Translate contentKey="lumiApp.ticketFile.originalName">Original Name</Translate>
            </span>
          </dt>
          <dd>{ticketFileEntity.originalName}</dd>
          <dt>
            <span id="contentType">
              <Translate contentKey="lumiApp.ticketFile.contentType">Content Type</Translate>
            </span>
          </dt>
          <dd>{ticketFileEntity.contentType}</dd>
          <dt>
            <span id="capacity">
              <Translate contentKey="lumiApp.ticketFile.capacity">Capacity</Translate>
            </span>
          </dt>
          <dd>{ticketFileEntity.capacity}</dd>
          <dt>
            <span id="storageType">
              <Translate contentKey="lumiApp.ticketFile.storageType">Storage Type</Translate>
            </span>
          </dt>
          <dd>{ticketFileEntity.storageType}</dd>
          <dt>
            <span id="path">
              <Translate contentKey="lumiApp.ticketFile.path">Path</Translate>
            </span>
          </dt>
          <dd>{ticketFileEntity.path}</dd>
          <dt>
            <span id="url">
              <Translate contentKey="lumiApp.ticketFile.url">Url</Translate>
            </span>
          </dt>
          <dd>{ticketFileEntity.url}</dd>
          <dt>
            <span id="checksum">
              <Translate contentKey="lumiApp.ticketFile.checksum">Checksum</Translate>
            </span>
          </dt>
          <dd>{ticketFileEntity.checksum}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.ticketFile.status">Status</Translate>
            </span>
          </dt>
          <dd>{ticketFileEntity.status}</dd>
          <dt>
            <span id="uploadedAt">
              <Translate contentKey="lumiApp.ticketFile.uploadedAt">Uploaded At</Translate>
            </span>
          </dt>
          <dd>
            {ticketFileEntity.uploadedAt ? <TextFormat value={ticketFileEntity.uploadedAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.ticketFile.ticket">Ticket</Translate>
          </dt>
          <dd>{ticketFileEntity.ticket ? ticketFileEntity.ticket.code : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.ticketFile.uploader">Uploader</Translate>
          </dt>
          <dd>{ticketFileEntity.uploader ? ticketFileEntity.uploader.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/ticket-file" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/ticket-file/${ticketFileEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TicketFileDetail;
