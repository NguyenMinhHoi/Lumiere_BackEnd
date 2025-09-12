import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { OrderStatus } from 'app/shared/model/enumerations/order-status.model';
import { PaymentStatus } from 'app/shared/model/enumerations/payment-status.model';
import { FulfillmentStatus } from 'app/shared/model/enumerations/fulfillment-status.model';
import { createEntity, getEntity, reset, updateEntity } from './orders.reducer';

export const OrdersUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const ordersEntity = useAppSelector(state => state.orders.entity);
  const loading = useAppSelector(state => state.orders.loading);
  const updating = useAppSelector(state => state.orders.updating);
  const updateSuccess = useAppSelector(state => state.orders.updateSuccess);
  const orderStatusValues = Object.keys(OrderStatus);
  const paymentStatusValues = Object.keys(PaymentStatus);
  const fulfillmentStatusValues = Object.keys(FulfillmentStatus);

  const handleClose = () => {
    navigate(`/orders${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.customerId !== undefined && typeof values.customerId !== 'number') {
      values.customerId = Number(values.customerId);
    }
    if (values.totalAmount !== undefined && typeof values.totalAmount !== 'number') {
      values.totalAmount = Number(values.totalAmount);
    }
    values.placedAt = convertDateTimeToServer(values.placedAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...ordersEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          placedAt: displayDefaultDateTime(),
          updatedAt: displayDefaultDateTime(),
        }
      : {
          status: 'DRAFT',
          paymentStatus: 'UNPAID',
          fulfillmentStatus: 'UNFULFILLED',
          ...ordersEntity,
          placedAt: convertDateTimeFromServer(ordersEntity.placedAt),
          updatedAt: convertDateTimeFromServer(ordersEntity.updatedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.orders.home.createOrEditLabel" data-cy="OrdersCreateUpdateHeading">
            <Translate contentKey="lumiApp.orders.home.createOrEditLabel">Create or edit a Orders</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="orders-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.orders.customerId')}
                id="orders-customerId"
                name="customerId"
                data-cy="customerId"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.orders.code')}
                id="orders-code"
                name="code"
                data-cy="code"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 6, message: translate('entity.validation.minlength', { min: 6 }) },
                  maxLength: { value: 32, message: translate('entity.validation.maxlength', { max: 32 }) },
                }}
              />
              <ValidatedField label={translate('lumiApp.orders.status')} id="orders-status" name="status" data-cy="status" type="select">
                {orderStatusValues.map(orderStatus => (
                  <option value={orderStatus} key={orderStatus}>
                    {translate(`lumiApp.OrderStatus.${orderStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.orders.paymentStatus')}
                id="orders-paymentStatus"
                name="paymentStatus"
                data-cy="paymentStatus"
                type="select"
              >
                {paymentStatusValues.map(paymentStatus => (
                  <option value={paymentStatus} key={paymentStatus}>
                    {translate(`lumiApp.PaymentStatus.${paymentStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.orders.fulfillmentStatus')}
                id="orders-fulfillmentStatus"
                name="fulfillmentStatus"
                data-cy="fulfillmentStatus"
                type="select"
              >
                {fulfillmentStatusValues.map(fulfillmentStatus => (
                  <option value={fulfillmentStatus} key={fulfillmentStatus}>
                    {translate(`lumiApp.FulfillmentStatus.${fulfillmentStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.orders.totalAmount')}
                id="orders-totalAmount"
                name="totalAmount"
                data-cy="totalAmount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.orders.currency')}
                id="orders-currency"
                name="currency"
                data-cy="currency"
                type="text"
                validate={{
                  maxLength: { value: 3, message: translate('entity.validation.maxlength', { max: 3 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.orders.note')}
                id="orders-note"
                name="note"
                data-cy="note"
                type="text"
                validate={{
                  maxLength: { value: 500, message: translate('entity.validation.maxlength', { max: 500 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.orders.placedAt')}
                id="orders-placedAt"
                name="placedAt"
                data-cy="placedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.orders.updatedAt')}
                id="orders-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/orders" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default OrdersUpdate;
