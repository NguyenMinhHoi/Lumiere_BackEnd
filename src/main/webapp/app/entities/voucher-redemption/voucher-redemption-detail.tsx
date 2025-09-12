import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './voucher-redemption.reducer';

export const VoucherRedemptionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const voucherRedemptionEntity = useAppSelector(state => state.voucherRedemption.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="voucherRedemptionDetailsHeading">
          <Translate contentKey="lumiApp.voucherRedemption.detail.title">VoucherRedemption</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{voucherRedemptionEntity.id}</dd>
          <dt>
            <span id="voucherId">
              <Translate contentKey="lumiApp.voucherRedemption.voucherId">Voucher Id</Translate>
            </span>
          </dt>
          <dd>{voucherRedemptionEntity.voucherId}</dd>
          <dt>
            <span id="orderId">
              <Translate contentKey="lumiApp.voucherRedemption.orderId">Order Id</Translate>
            </span>
          </dt>
          <dd>{voucherRedemptionEntity.orderId}</dd>
          <dt>
            <span id="customerId">
              <Translate contentKey="lumiApp.voucherRedemption.customerId">Customer Id</Translate>
            </span>
          </dt>
          <dd>{voucherRedemptionEntity.customerId}</dd>
          <dt>
            <span id="redeemedAt">
              <Translate contentKey="lumiApp.voucherRedemption.redeemedAt">Redeemed At</Translate>
            </span>
          </dt>
          <dd>
            {voucherRedemptionEntity.redeemedAt ? (
              <TextFormat value={voucherRedemptionEntity.redeemedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
        </dl>
        <Button tag={Link} to="/voucher-redemption" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/voucher-redemption/${voucherRedemptionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default VoucherRedemptionDetail;
