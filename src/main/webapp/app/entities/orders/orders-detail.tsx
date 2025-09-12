import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './orders.reducer';

export const OrdersDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const ordersEntity = useAppSelector(state => state.orders.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="ordersDetailsHeading">
          <Translate contentKey="lumiApp.orders.detail.title">Orders</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{ordersEntity.id}</dd>
          <dt>
            <span id="customerId">
              <Translate contentKey="lumiApp.orders.customerId">Customer Id</Translate>
            </span>
          </dt>
          <dd>{ordersEntity.customerId}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="lumiApp.orders.code">Code</Translate>
            </span>
          </dt>
          <dd>{ordersEntity.code}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.orders.status">Status</Translate>
            </span>
          </dt>
          <dd>{ordersEntity.status}</dd>
          <dt>
            <span id="paymentStatus">
              <Translate contentKey="lumiApp.orders.paymentStatus">Payment Status</Translate>
            </span>
          </dt>
          <dd>{ordersEntity.paymentStatus}</dd>
          <dt>
            <span id="fulfillmentStatus">
              <Translate contentKey="lumiApp.orders.fulfillmentStatus">Fulfillment Status</Translate>
            </span>
          </dt>
          <dd>{ordersEntity.fulfillmentStatus}</dd>
          <dt>
            <span id="totalAmount">
              <Translate contentKey="lumiApp.orders.totalAmount">Total Amount</Translate>
            </span>
          </dt>
          <dd>{ordersEntity.totalAmount}</dd>
          <dt>
            <span id="currency">
              <Translate contentKey="lumiApp.orders.currency">Currency</Translate>
            </span>
          </dt>
          <dd>{ordersEntity.currency}</dd>
          <dt>
            <span id="note">
              <Translate contentKey="lumiApp.orders.note">Note</Translate>
            </span>
          </dt>
          <dd>{ordersEntity.note}</dd>
          <dt>
            <span id="placedAt">
              <Translate contentKey="lumiApp.orders.placedAt">Placed At</Translate>
            </span>
          </dt>
          <dd>{ordersEntity.placedAt ? <TextFormat value={ordersEntity.placedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.orders.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>{ordersEntity.updatedAt ? <TextFormat value={ordersEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/orders" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/orders/${ordersEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default OrdersDetail;
