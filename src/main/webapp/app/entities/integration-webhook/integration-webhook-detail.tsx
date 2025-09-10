import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './integration-webhook.reducer';

export const IntegrationWebhookDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const integrationWebhookEntity = useAppSelector(state => state.integrationWebhook.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="integrationWebhookDetailsHeading">
          <Translate contentKey="lumiApp.integrationWebhook.detail.title">IntegrationWebhook</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{integrationWebhookEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="lumiApp.integrationWebhook.name">Name</Translate>
            </span>
          </dt>
          <dd>{integrationWebhookEntity.name}</dd>
          <dt>
            <span id="targetUrl">
              <Translate contentKey="lumiApp.integrationWebhook.targetUrl">Target Url</Translate>
            </span>
          </dt>
          <dd>{integrationWebhookEntity.targetUrl}</dd>
          <dt>
            <span id="secret">
              <Translate contentKey="lumiApp.integrationWebhook.secret">Secret</Translate>
            </span>
          </dt>
          <dd>{integrationWebhookEntity.secret}</dd>
          <dt>
            <span id="isActive">
              <Translate contentKey="lumiApp.integrationWebhook.isActive">Is Active</Translate>
            </span>
          </dt>
          <dd>{integrationWebhookEntity.isActive ? 'true' : 'false'}</dd>
          <dt>
            <span id="subscribedEvents">
              <Translate contentKey="lumiApp.integrationWebhook.subscribedEvents">Subscribed Events</Translate>
            </span>
          </dt>
          <dd>{integrationWebhookEntity.subscribedEvents}</dd>
        </dl>
        <Button tag={Link} to="/integration-webhook" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/integration-webhook/${integrationWebhookEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default IntegrationWebhookDetail;
