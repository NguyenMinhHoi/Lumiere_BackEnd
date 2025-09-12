import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './warehouse.reducer';

export const WarehouseDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const warehouseEntity = useAppSelector(state => state.warehouse.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="warehouseDetailsHeading">
          <Translate contentKey="lumiApp.warehouse.detail.title">Warehouse</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{warehouseEntity.id}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="lumiApp.warehouse.code">Code</Translate>
            </span>
          </dt>
          <dd>{warehouseEntity.code}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="lumiApp.warehouse.name">Name</Translate>
            </span>
          </dt>
          <dd>{warehouseEntity.name}</dd>
          <dt>
            <span id="address">
              <Translate contentKey="lumiApp.warehouse.address">Address</Translate>
            </span>
          </dt>
          <dd>{warehouseEntity.address}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.warehouse.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {warehouseEntity.createdAt ? <TextFormat value={warehouseEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.warehouse.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>
            {warehouseEntity.updatedAt ? <TextFormat value={warehouseEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
        </dl>
        <Button tag={Link} to="/warehouse" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/warehouse/${warehouseEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default WarehouseDetail;
