import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './cloth-stock-movement.reducer';

export const ClothStockMovementDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const clothStockMovementEntity = useAppSelector(state => state.clothStockMovement.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="clothStockMovementDetailsHeading">
          <Translate contentKey="lumiApp.clothStockMovement.detail.title">ClothStockMovement</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{clothStockMovementEntity.id}</dd>
          <dt>
            <span id="clothId">
              <Translate contentKey="lumiApp.clothStockMovement.clothId">Cloth Id</Translate>
            </span>
          </dt>
          <dd>{clothStockMovementEntity.clothId}</dd>
          <dt>
            <span id="warehouseId">
              <Translate contentKey="lumiApp.clothStockMovement.warehouseId">Warehouse Id</Translate>
            </span>
          </dt>
          <dd>{clothStockMovementEntity.warehouseId}</dd>
          <dt>
            <span id="delta">
              <Translate contentKey="lumiApp.clothStockMovement.delta">Delta</Translate>
            </span>
          </dt>
          <dd>{clothStockMovementEntity.delta}</dd>
          <dt>
            <span id="reason">
              <Translate contentKey="lumiApp.clothStockMovement.reason">Reason</Translate>
            </span>
          </dt>
          <dd>{clothStockMovementEntity.reason}</dd>
          <dt>
            <span id="refOrderId">
              <Translate contentKey="lumiApp.clothStockMovement.refOrderId">Ref Order Id</Translate>
            </span>
          </dt>
          <dd>{clothStockMovementEntity.refOrderId}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.clothStockMovement.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {clothStockMovementEntity.createdAt ? (
              <TextFormat value={clothStockMovementEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
        </dl>
        <Button tag={Link} to="/cloth-stock-movement" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/cloth-stock-movement/${clothStockMovementEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ClothStockMovementDetail;
