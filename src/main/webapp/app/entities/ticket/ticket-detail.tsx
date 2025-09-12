import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './ticket.reducer';

export const TicketDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const ticketEntity = useAppSelector(state => state.ticket.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="ticketDetailsHeading">
          <Translate contentKey="lumiApp.ticket.detail.title">Ticket</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{ticketEntity.id}</dd>
          <dt>
            <span id="customerId">
              <Translate contentKey="lumiApp.ticket.customerId">Customer Id</Translate>
            </span>
          </dt>
          <dd>{ticketEntity.customerId}</dd>
          <dt>
            <span id="slaPlanId">
              <Translate contentKey="lumiApp.ticket.slaPlanId">Sla Plan Id</Translate>
            </span>
          </dt>
          <dd>{ticketEntity.slaPlanId}</dd>
          <dt>
            <span id="orderId">
              <Translate contentKey="lumiApp.ticket.orderId">Order Id</Translate>
            </span>
          </dt>
          <dd>{ticketEntity.orderId}</dd>
          <dt>
            <span id="assigneeEmployeeId">
              <Translate contentKey="lumiApp.ticket.assigneeEmployeeId">Assignee Employee Id</Translate>
            </span>
          </dt>
          <dd>{ticketEntity.assigneeEmployeeId}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="lumiApp.ticket.code">Code</Translate>
            </span>
          </dt>
          <dd>{ticketEntity.code}</dd>
          <dt>
            <span id="subject">
              <Translate contentKey="lumiApp.ticket.subject">Subject</Translate>
            </span>
          </dt>
          <dd>{ticketEntity.subject}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="lumiApp.ticket.description">Description</Translate>
            </span>
          </dt>
          <dd>{ticketEntity.description}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.ticket.status">Status</Translate>
            </span>
          </dt>
          <dd>{ticketEntity.status}</dd>
          <dt>
            <span id="priority">
              <Translate contentKey="lumiApp.ticket.priority">Priority</Translate>
            </span>
          </dt>
          <dd>{ticketEntity.priority}</dd>
          <dt>
            <span id="channel">
              <Translate contentKey="lumiApp.ticket.channel">Channel</Translate>
            </span>
          </dt>
          <dd>{ticketEntity.channel}</dd>
          <dt>
            <span id="openedAt">
              <Translate contentKey="lumiApp.ticket.openedAt">Opened At</Translate>
            </span>
          </dt>
          <dd>{ticketEntity.openedAt ? <TextFormat value={ticketEntity.openedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="firstResponseAt">
              <Translate contentKey="lumiApp.ticket.firstResponseAt">First Response At</Translate>
            </span>
          </dt>
          <dd>
            {ticketEntity.firstResponseAt ? <TextFormat value={ticketEntity.firstResponseAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="resolvedAt">
              <Translate contentKey="lumiApp.ticket.resolvedAt">Resolved At</Translate>
            </span>
          </dt>
          <dd>{ticketEntity.resolvedAt ? <TextFormat value={ticketEntity.resolvedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="slaDueAt">
              <Translate contentKey="lumiApp.ticket.slaDueAt">Sla Due At</Translate>
            </span>
          </dt>
          <dd>{ticketEntity.slaDueAt ? <TextFormat value={ticketEntity.slaDueAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/ticket" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/ticket/${ticketEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TicketDetail;
