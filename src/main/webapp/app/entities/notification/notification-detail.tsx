import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './notification.reducer';

export const NotificationDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const notificationEntity = useAppSelector(state => state.notification.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="notificationDetailsHeading">
          <Translate contentKey="lumiApp.notification.detail.title">Notification</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{notificationEntity.id}</dd>
          <dt>
            <span id="ticketId">
              <Translate contentKey="lumiApp.notification.ticketId">Ticket Id</Translate>
            </span>
          </dt>
          <dd>{notificationEntity.ticketId}</dd>
          <dt>
            <span id="customerId">
              <Translate contentKey="lumiApp.notification.customerId">Customer Id</Translate>
            </span>
          </dt>
          <dd>{notificationEntity.customerId}</dd>
          <dt>
            <span id="surveyId">
              <Translate contentKey="lumiApp.notification.surveyId">Survey Id</Translate>
            </span>
          </dt>
          <dd>{notificationEntity.surveyId}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="lumiApp.notification.type">Type</Translate>
            </span>
          </dt>
          <dd>{notificationEntity.type}</dd>
          <dt>
            <span id="channel">
              <Translate contentKey="lumiApp.notification.channel">Channel</Translate>
            </span>
          </dt>
          <dd>{notificationEntity.channel}</dd>
          <dt>
            <span id="subject">
              <Translate contentKey="lumiApp.notification.subject">Subject</Translate>
            </span>
          </dt>
          <dd>{notificationEntity.subject}</dd>
          <dt>
            <span id="payload">
              <Translate contentKey="lumiApp.notification.payload">Payload</Translate>
            </span>
          </dt>
          <dd>{notificationEntity.payload}</dd>
          <dt>
            <span id="sendStatus">
              <Translate contentKey="lumiApp.notification.sendStatus">Send Status</Translate>
            </span>
          </dt>
          <dd>{notificationEntity.sendStatus}</dd>
          <dt>
            <span id="retryCount">
              <Translate contentKey="lumiApp.notification.retryCount">Retry Count</Translate>
            </span>
          </dt>
          <dd>{notificationEntity.retryCount}</dd>
          <dt>
            <span id="lastTriedAt">
              <Translate contentKey="lumiApp.notification.lastTriedAt">Last Tried At</Translate>
            </span>
          </dt>
          <dd>
            {notificationEntity.lastTriedAt ? (
              <TextFormat value={notificationEntity.lastTriedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.notification.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {notificationEntity.createdAt ? <TextFormat value={notificationEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
        </dl>
        <Button tag={Link} to="/notification" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/notification/${notificationEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default NotificationDetail;
