import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './supplement.reducer';

export const SupplementDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const supplementEntity = useAppSelector(state => state.supplement.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="supplementDetailsHeading">
          <Translate contentKey="lumiApp.supplement.detail.title">Supplement</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{supplementEntity.id}</dd>
          <dt>
            <span id="supplyPrice">
              <Translate contentKey="lumiApp.supplement.supplyPrice">Supply Price</Translate>
            </span>
          </dt>
          <dd>{supplementEntity.supplyPrice}</dd>
          <dt>
            <span id="currency">
              <Translate contentKey="lumiApp.supplement.currency">Currency</Translate>
            </span>
          </dt>
          <dd>{supplementEntity.currency}</dd>
          <dt>
            <span id="leadTimeDays">
              <Translate contentKey="lumiApp.supplement.leadTimeDays">Lead Time Days</Translate>
            </span>
          </dt>
          <dd>{supplementEntity.leadTimeDays}</dd>
          <dt>
            <span id="minOrderQty">
              <Translate contentKey="lumiApp.supplement.minOrderQty">Min Order Qty</Translate>
            </span>
          </dt>
          <dd>{supplementEntity.minOrderQty}</dd>
          <dt>
            <span id="isPreferred">
              <Translate contentKey="lumiApp.supplement.isPreferred">Is Preferred</Translate>
            </span>
          </dt>
          <dd>{supplementEntity.isPreferred ? 'true' : 'false'}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.supplement.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {supplementEntity.createdAt ? <TextFormat value={supplementEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.supplement.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>
            {supplementEntity.updatedAt ? <TextFormat value={supplementEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.supplement.product">Product</Translate>
          </dt>
          <dd>{supplementEntity.product ? supplementEntity.product.code : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.supplement.supplier">Supplier</Translate>
          </dt>
          <dd>{supplementEntity.supplier ? supplementEntity.supplier.code : ''}</dd>
        </dl>
        <Button tag={Link} to="/supplement" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/supplement/${supplementEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default SupplementDetail;
