import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './customer.reducer';

export const CustomerDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const customerEntity = useAppSelector(state => state.customer.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="customerDetailsHeading">
          <Translate contentKey="lumiApp.customer.detail.title">Customer</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{customerEntity.id}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="lumiApp.customer.code">Code</Translate>
            </span>
          </dt>
          <dd>{customerEntity.code}</dd>
          <dt>
            <span id="fullName">
              <Translate contentKey="lumiApp.customer.fullName">Full Name</Translate>
            </span>
          </dt>
          <dd>{customerEntity.fullName}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="lumiApp.customer.email">Email</Translate>
            </span>
          </dt>
          <dd>{customerEntity.email}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="lumiApp.customer.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{customerEntity.phone}</dd>
          <dt>
            <span id="tier">
              <Translate contentKey="lumiApp.customer.tier">Tier</Translate>
            </span>
          </dt>
          <dd>{customerEntity.tier}</dd>
          <dt>
            <span id="points">
              <Translate contentKey="lumiApp.customer.points">Points</Translate>
            </span>
          </dt>
          <dd>{customerEntity.points}</dd>
          <dt>
            <span id="dob">
              <Translate contentKey="lumiApp.customer.dob">Dob</Translate>
            </span>
          </dt>
          <dd>{customerEntity.dob ? <TextFormat value={customerEntity.dob} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="address">
              <Translate contentKey="lumiApp.customer.address">Address</Translate>
            </span>
          </dt>
          <dd>{customerEntity.address}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.customer.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{customerEntity.createdAt ? <TextFormat value={customerEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.customer.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>{customerEntity.updatedAt ? <TextFormat value={customerEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/customer" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/customer/${customerEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CustomerDetail;
