import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './cloth-audit.reducer';

export const ClothAuditDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const clothAuditEntity = useAppSelector(state => state.clothAudit.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="clothAuditDetailsHeading">
          <Translate contentKey="lumiApp.clothAudit.detail.title">ClothAudit</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{clothAuditEntity.id}</dd>
          <dt>
            <span id="clothId">
              <Translate contentKey="lumiApp.clothAudit.clothId">Cloth Id</Translate>
            </span>
          </dt>
          <dd>{clothAuditEntity.clothId}</dd>
          <dt>
            <span id="supplierId">
              <Translate contentKey="lumiApp.clothAudit.supplierId">Supplier Id</Translate>
            </span>
          </dt>
          <dd>{clothAuditEntity.supplierId}</dd>
          <dt>
            <span id="productId">
              <Translate contentKey="lumiApp.clothAudit.productId">Product Id</Translate>
            </span>
          </dt>
          <dd>{clothAuditEntity.productId}</dd>
          <dt>
            <span id="action">
              <Translate contentKey="lumiApp.clothAudit.action">Action</Translate>
            </span>
          </dt>
          <dd>{clothAuditEntity.action}</dd>
          <dt>
            <span id="quantity">
              <Translate contentKey="lumiApp.clothAudit.quantity">Quantity</Translate>
            </span>
          </dt>
          <dd>{clothAuditEntity.quantity}</dd>
          <dt>
            <span id="unit">
              <Translate contentKey="lumiApp.clothAudit.unit">Unit</Translate>
            </span>
          </dt>
          <dd>{clothAuditEntity.unit}</dd>
          <dt>
            <span id="sentAt">
              <Translate contentKey="lumiApp.clothAudit.sentAt">Sent At</Translate>
            </span>
          </dt>
          <dd>{clothAuditEntity.sentAt ? <TextFormat value={clothAuditEntity.sentAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="note">
              <Translate contentKey="lumiApp.clothAudit.note">Note</Translate>
            </span>
          </dt>
          <dd>{clothAuditEntity.note}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.clothAudit.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {clothAuditEntity.createdAt ? <TextFormat value={clothAuditEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
        </dl>
        <Button tag={Link} to="/cloth-audit" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/cloth-audit/${clothAuditEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ClothAuditDetail;
