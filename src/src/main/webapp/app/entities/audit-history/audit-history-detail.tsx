import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './audit-history.reducer';

export const AuditHistoryDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const auditHistoryEntity = useAppSelector(state => state.auditHistory.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="auditHistoryDetailsHeading">
          <Translate contentKey="lumiApp.auditHistory.detail.title">AuditHistory</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{auditHistoryEntity.id}</dd>
          <dt>
            <span id="entityName">
              <Translate contentKey="lumiApp.auditHistory.entityName">Entity Name</Translate>
            </span>
          </dt>
          <dd>{auditHistoryEntity.entityName}</dd>
          <dt>
            <span id="entityId">
              <Translate contentKey="lumiApp.auditHistory.entityId">Entity Id</Translate>
            </span>
          </dt>
          <dd>{auditHistoryEntity.entityId}</dd>
          <dt>
            <span id="action">
              <Translate contentKey="lumiApp.auditHistory.action">Action</Translate>
            </span>
          </dt>
          <dd>{auditHistoryEntity.action}</dd>
          <dt>
            <span id="oldValue">
              <Translate contentKey="lumiApp.auditHistory.oldValue">Old Value</Translate>
            </span>
          </dt>
          <dd>{auditHistoryEntity.oldValue}</dd>
          <dt>
            <span id="newValue">
              <Translate contentKey="lumiApp.auditHistory.newValue">New Value</Translate>
            </span>
          </dt>
          <dd>{auditHistoryEntity.newValue}</dd>
          <dt>
            <span id="performedBy">
              <Translate contentKey="lumiApp.auditHistory.performedBy">Performed By</Translate>
            </span>
          </dt>
          <dd>{auditHistoryEntity.performedBy}</dd>
          <dt>
            <span id="performedAt">
              <Translate contentKey="lumiApp.auditHistory.performedAt">Performed At</Translate>
            </span>
          </dt>
          <dd>
            {auditHistoryEntity.performedAt ? (
              <TextFormat value={auditHistoryEntity.performedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="ipAddress">
              <Translate contentKey="lumiApp.auditHistory.ipAddress">Ip Address</Translate>
            </span>
          </dt>
          <dd>{auditHistoryEntity.ipAddress}</dd>
        </dl>
        <Button tag={Link} to="/audit-history" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/audit-history/${auditHistoryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AuditHistoryDetail;
