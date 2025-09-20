import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './cloth-product-map.reducer';

export const ClothProductMapDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const clothProductMapEntity = useAppSelector(state => state.clothProductMap.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="clothProductMapDetailsHeading">
          <Translate contentKey="lumiApp.clothProductMap.detail.title">ClothProductMap</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{clothProductMapEntity.id}</dd>
          <dt>
            <span id="clothId">
              <Translate contentKey="lumiApp.clothProductMap.clothId">Cloth Id</Translate>
            </span>
          </dt>
          <dd>{clothProductMapEntity.clothId}</dd>
          <dt>
            <span id="productId">
              <Translate contentKey="lumiApp.clothProductMap.productId">Product Id</Translate>
            </span>
          </dt>
          <dd>{clothProductMapEntity.productId}</dd>
          <dt>
            <span id="quantity">
              <Translate contentKey="lumiApp.clothProductMap.quantity">Quantity</Translate>
            </span>
          </dt>
          <dd>{clothProductMapEntity.quantity}</dd>
          <dt>
            <span id="unit">
              <Translate contentKey="lumiApp.clothProductMap.unit">Unit</Translate>
            </span>
          </dt>
          <dd>{clothProductMapEntity.unit}</dd>
          <dt>
            <span id="note">
              <Translate contentKey="lumiApp.clothProductMap.note">Note</Translate>
            </span>
          </dt>
          <dd>{clothProductMapEntity.note}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.clothProductMap.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {clothProductMapEntity.createdAt ? (
              <TextFormat value={clothProductMapEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
        </dl>
        <Button tag={Link} to="/cloth-product-map" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/cloth-product-map/${clothProductMapEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ClothProductMapDetail;
