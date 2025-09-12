import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './integration-log.reducer';

export const IntegrationLogDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const integrationLogEntity = useAppSelector(state => state.integrationLog.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="integrationLogDetailsHeading">
          <Translate contentKey="lumiApp.integrationLog.detail.title">IntegrationLog</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{integrationLogEntity.id}</dd>
          <dt>
            <span id="sourceApp">
              <Translate contentKey="lumiApp.integrationLog.sourceApp">Source App</Translate>
            </span>
          </dt>
          <dd>{integrationLogEntity.sourceApp}</dd>
          <dt>
            <span id="targetApp">
              <Translate contentKey="lumiApp.integrationLog.targetApp">Target App</Translate>
            </span>
          </dt>
          <dd>{integrationLogEntity.targetApp}</dd>
          <dt>
            <span id="payload">
              <Translate contentKey="lumiApp.integrationLog.payload">Payload</Translate>
            </span>
          </dt>
          <dd>{integrationLogEntity.payload}</dd>
          <dt>
            <span id="response">
              <Translate contentKey="lumiApp.integrationLog.response">Response</Translate>
            </span>
          </dt>
          <dd>{integrationLogEntity.response}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.integrationLog.status">Status</Translate>
            </span>
          </dt>
          <dd>{integrationLogEntity.status}</dd>
          <dt>
            <span id="retries">
              <Translate contentKey="lumiApp.integrationLog.retries">Retries</Translate>
            </span>
          </dt>
          <dd>{integrationLogEntity.retries}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.integrationLog.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {integrationLogEntity.createdAt ? (
              <TextFormat value={integrationLogEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.integrationLog.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>
            {integrationLogEntity.updatedAt ? (
              <TextFormat value={integrationLogEntity.updatedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
        </dl>
        <Button tag={Link} to="/integration-log" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/integration-log/${integrationLogEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default IntegrationLogDetail;
