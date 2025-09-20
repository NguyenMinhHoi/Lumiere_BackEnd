import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './cloth-inventory.reducer';

export const ClothInventoryDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const clothInventoryEntity = useAppSelector(state => state.clothInventory.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="clothInventoryDetailsHeading">
          <Translate contentKey="lumiApp.clothInventory.detail.title">ClothInventory</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{clothInventoryEntity.id}</dd>
          <dt>
            <span id="clothId">
              <Translate contentKey="lumiApp.clothInventory.clothId">Cloth Id</Translate>
            </span>
          </dt>
          <dd>{clothInventoryEntity.clothId}</dd>
          <dt>
            <span id="warehouseId">
              <Translate contentKey="lumiApp.clothInventory.warehouseId">Warehouse Id</Translate>
            </span>
          </dt>
          <dd>{clothInventoryEntity.warehouseId}</dd>
          <dt>
            <span id="quantity">
              <Translate contentKey="lumiApp.clothInventory.quantity">Quantity</Translate>
            </span>
          </dt>
          <dd>{clothInventoryEntity.quantity}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.clothInventory.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>
            {clothInventoryEntity.updatedAt ? (
              <TextFormat value={clothInventoryEntity.updatedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
        </dl>
        <Button tag={Link} to="/cloth-inventory" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/cloth-inventory/${clothInventoryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ClothInventoryDetail;
