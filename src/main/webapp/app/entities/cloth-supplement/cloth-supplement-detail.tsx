import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './cloth-supplement.reducer';

export const ClothSupplementDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const clothSupplementEntity = useAppSelector(state => state.clothSupplement.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="clothSupplementDetailsHeading">
          <Translate contentKey="lumiApp.clothSupplement.detail.title">ClothSupplement</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{clothSupplementEntity.id}</dd>
          <dt>
            <span id="clothId">
              <Translate contentKey="lumiApp.clothSupplement.clothId">Cloth Id</Translate>
            </span>
          </dt>
          <dd>{clothSupplementEntity.clothId}</dd>
          <dt>
            <span id="supplierId">
              <Translate contentKey="lumiApp.clothSupplement.supplierId">Supplier Id</Translate>
            </span>
          </dt>
          <dd>{clothSupplementEntity.supplierId}</dd>
          <dt>
            <span id="supplyPrice">
              <Translate contentKey="lumiApp.clothSupplement.supplyPrice">Supply Price</Translate>
            </span>
          </dt>
          <dd>{clothSupplementEntity.supplyPrice}</dd>
          <dt>
            <span id="currency">
              <Translate contentKey="lumiApp.clothSupplement.currency">Currency</Translate>
            </span>
          </dt>
          <dd>{clothSupplementEntity.currency}</dd>
          <dt>
            <span id="leadTimeDays">
              <Translate contentKey="lumiApp.clothSupplement.leadTimeDays">Lead Time Days</Translate>
            </span>
          </dt>
          <dd>{clothSupplementEntity.leadTimeDays}</dd>
          <dt>
            <span id="minOrderQty">
              <Translate contentKey="lumiApp.clothSupplement.minOrderQty">Min Order Qty</Translate>
            </span>
          </dt>
          <dd>{clothSupplementEntity.minOrderQty}</dd>
          <dt>
            <span id="isPreferred">
              <Translate contentKey="lumiApp.clothSupplement.isPreferred">Is Preferred</Translate>
            </span>
          </dt>
          <dd>{clothSupplementEntity.isPreferred ? 'true' : 'false'}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.clothSupplement.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {clothSupplementEntity.createdAt ? (
              <TextFormat value={clothSupplementEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.clothSupplement.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>
            {clothSupplementEntity.updatedAt ? (
              <TextFormat value={clothSupplementEntity.updatedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
        </dl>
        <Button tag={Link} to="/cloth-supplement" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/cloth-supplement/${clothSupplementEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ClothSupplementDetail;
