import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './supplier.reducer';

export const SupplierDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const supplierEntity = useAppSelector(state => state.supplier.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="supplierDetailsHeading">
          <Translate contentKey="lumiApp.supplier.detail.title">Supplier</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{supplierEntity.id}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="lumiApp.supplier.code">Code</Translate>
            </span>
          </dt>
          <dd>{supplierEntity.code}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="lumiApp.supplier.name">Name</Translate>
            </span>
          </dt>
          <dd>{supplierEntity.name}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="lumiApp.supplier.email">Email</Translate>
            </span>
          </dt>
          <dd>{supplierEntity.email}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="lumiApp.supplier.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{supplierEntity.phone}</dd>
          <dt>
            <span id="address">
              <Translate contentKey="lumiApp.supplier.address">Address</Translate>
            </span>
          </dt>
          <dd>{supplierEntity.address}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.supplier.status">Status</Translate>
            </span>
          </dt>
          <dd>{supplierEntity.status}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.supplier.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{supplierEntity.createdAt ? <TextFormat value={supplierEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.supplier.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>{supplierEntity.updatedAt ? <TextFormat value={supplierEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/supplier" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/supplier/${supplierEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default SupplierDetail;
