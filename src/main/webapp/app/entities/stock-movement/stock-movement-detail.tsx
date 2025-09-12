import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './stock-movement.reducer';

export const StockMovementDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const stockMovementEntity = useAppSelector(state => state.stockMovement.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="stockMovementDetailsHeading">
          <Translate contentKey="lumiApp.stockMovement.detail.title">StockMovement</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{stockMovementEntity.id}</dd>
          <dt>
            <span id="productVariantId">
              <Translate contentKey="lumiApp.stockMovement.productVariantId">Product Variant Id</Translate>
            </span>
          </dt>
          <dd>{stockMovementEntity.productVariantId}</dd>
          <dt>
            <span id="warehouseId">
              <Translate contentKey="lumiApp.stockMovement.warehouseId">Warehouse Id</Translate>
            </span>
          </dt>
          <dd>{stockMovementEntity.warehouseId}</dd>
          <dt>
            <span id="delta">
              <Translate contentKey="lumiApp.stockMovement.delta">Delta</Translate>
            </span>
          </dt>
          <dd>{stockMovementEntity.delta}</dd>
          <dt>
            <span id="reason">
              <Translate contentKey="lumiApp.stockMovement.reason">Reason</Translate>
            </span>
          </dt>
          <dd>{stockMovementEntity.reason}</dd>
          <dt>
            <span id="refOrderId">
              <Translate contentKey="lumiApp.stockMovement.refOrderId">Ref Order Id</Translate>
            </span>
          </dt>
          <dd>{stockMovementEntity.refOrderId}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.stockMovement.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {stockMovementEntity.createdAt ? (
              <TextFormat value={stockMovementEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
        </dl>
        <Button tag={Link} to="/stock-movement" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/stock-movement/${stockMovementEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default StockMovementDetail;
