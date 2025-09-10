import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './ticket-comment.reducer';

export const TicketCommentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const ticketCommentEntity = useAppSelector(state => state.ticketComment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="ticketCommentDetailsHeading">
          <Translate contentKey="lumiApp.ticketComment.detail.title">TicketComment</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{ticketCommentEntity.id}</dd>
          <dt>
            <span id="body">
              <Translate contentKey="lumiApp.ticketComment.body">Body</Translate>
            </span>
          </dt>
          <dd>{ticketCommentEntity.body}</dd>
          <dt>
            <span id="visibility">
              <Translate contentKey="lumiApp.ticketComment.visibility">Visibility</Translate>
            </span>
          </dt>
          <dd>{ticketCommentEntity.visibility}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.ticketComment.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {ticketCommentEntity.createdAt ? (
              <TextFormat value={ticketCommentEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.ticketComment.ticket">Ticket</Translate>
          </dt>
          <dd>{ticketCommentEntity.ticket ? ticketCommentEntity.ticket.code : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.ticketComment.author">Author</Translate>
          </dt>
          <dd>{ticketCommentEntity.author ? ticketCommentEntity.author.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/ticket-comment" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/ticket-comment/${ticketCommentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TicketCommentDetail;
