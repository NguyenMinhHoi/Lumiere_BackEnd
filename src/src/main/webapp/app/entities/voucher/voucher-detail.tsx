import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './voucher.reducer';

export const VoucherDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const voucherEntity = useAppSelector(state => state.voucher.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="voucherDetailsHeading">
          <Translate contentKey="lumiApp.voucher.detail.title">Voucher</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{voucherEntity.id}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="lumiApp.voucher.code">Code</Translate>
            </span>
          </dt>
          <dd>{voucherEntity.code}</dd>
          <dt>
            <span id="discountType">
              <Translate contentKey="lumiApp.voucher.discountType">Discount Type</Translate>
            </span>
          </dt>
          <dd>{voucherEntity.discountType}</dd>
          <dt>
            <span id="discountValue">
              <Translate contentKey="lumiApp.voucher.discountValue">Discount Value</Translate>
            </span>
          </dt>
          <dd>{voucherEntity.discountValue}</dd>
          <dt>
            <span id="minOrderValue">
              <Translate contentKey="lumiApp.voucher.minOrderValue">Min Order Value</Translate>
            </span>
          </dt>
          <dd>{voucherEntity.minOrderValue}</dd>
          <dt>
            <span id="maxDiscountValue">
              <Translate contentKey="lumiApp.voucher.maxDiscountValue">Max Discount Value</Translate>
            </span>
          </dt>
          <dd>{voucherEntity.maxDiscountValue}</dd>
          <dt>
            <span id="usageLimit">
              <Translate contentKey="lumiApp.voucher.usageLimit">Usage Limit</Translate>
            </span>
          </dt>
          <dd>{voucherEntity.usageLimit}</dd>
          <dt>
            <span id="usedCount">
              <Translate contentKey="lumiApp.voucher.usedCount">Used Count</Translate>
            </span>
          </dt>
          <dd>{voucherEntity.usedCount}</dd>
          <dt>
            <span id="validFrom">
              <Translate contentKey="lumiApp.voucher.validFrom">Valid From</Translate>
            </span>
          </dt>
          <dd>{voucherEntity.validFrom ? <TextFormat value={voucherEntity.validFrom} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="validTo">
              <Translate contentKey="lumiApp.voucher.validTo">Valid To</Translate>
            </span>
          </dt>
          <dd>{voucherEntity.validTo ? <TextFormat value={voucherEntity.validTo} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.voucher.status">Status</Translate>
            </span>
          </dt>
          <dd>{voucherEntity.status}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.voucher.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{voucherEntity.createdAt ? <TextFormat value={voucherEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.voucher.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>{voucherEntity.updatedAt ? <TextFormat value={voucherEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/voucher" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/voucher/${voucherEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default VoucherDetail;
