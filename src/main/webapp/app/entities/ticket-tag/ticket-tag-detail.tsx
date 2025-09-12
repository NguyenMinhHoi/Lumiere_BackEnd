import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './ticket-tag.reducer';

export const TicketTagDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const ticketTagEntity = useAppSelector(state => state.ticketTag.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="ticketTagDetailsHeading">
          <Translate contentKey="lumiApp.ticketTag.detail.title">TicketTag</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{ticketTagEntity.id}</dd>
          <dt>
            <span id="ticketId">
              <Translate contentKey="lumiApp.ticketTag.ticketId">Ticket Id</Translate>
            </span>
          </dt>
          <dd>{ticketTagEntity.ticketId}</dd>
          <dt>
            <span id="tagId">
              <Translate contentKey="lumiApp.ticketTag.tagId">Tag Id</Translate>
            </span>
          </dt>
          <dd>{ticketTagEntity.tagId}</dd>
        </dl>
        <Button tag={Link} to="/ticket-tag" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/ticket-tag/${ticketTagEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TicketTagDetail;
