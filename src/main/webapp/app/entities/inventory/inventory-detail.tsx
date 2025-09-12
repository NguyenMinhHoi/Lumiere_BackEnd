import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './inventory.reducer';

export const InventoryDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const inventoryEntity = useAppSelector(state => state.inventory.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="inventoryDetailsHeading">
          <Translate contentKey="lumiApp.inventory.detail.title">Inventory</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{inventoryEntity.id}</dd>
          <dt>
            <span id="productVariantId">
              <Translate contentKey="lumiApp.inventory.productVariantId">Product Variant Id</Translate>
            </span>
          </dt>
          <dd>{inventoryEntity.productVariantId}</dd>
          <dt>
            <span id="warehouseId">
              <Translate contentKey="lumiApp.inventory.warehouseId">Warehouse Id</Translate>
            </span>
          </dt>
          <dd>{inventoryEntity.warehouseId}</dd>
          <dt>
            <span id="quantity">
              <Translate contentKey="lumiApp.inventory.quantity">Quantity</Translate>
            </span>
          </dt>
          <dd>{inventoryEntity.quantity}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.inventory.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>
            {inventoryEntity.updatedAt ? <TextFormat value={inventoryEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
        </dl>
        <Button tag={Link} to="/inventory" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/inventory/${inventoryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default InventoryDetail;
