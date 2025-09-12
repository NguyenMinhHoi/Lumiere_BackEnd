import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './voucher-redemption.reducer';

export const VoucherRedemptionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const voucherRedemptionEntity = useAppSelector(state => state.voucherRedemption.entity);
  const loading = useAppSelector(state => state.voucherRedemption.loading);
  const updating = useAppSelector(state => state.voucherRedemption.updating);
  const updateSuccess = useAppSelector(state => state.voucherRedemption.updateSuccess);

  const handleClose = () => {
    navigate(`/voucher-redemption${location.search}`);
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
    if (values.voucherId !== undefined && typeof values.voucherId !== 'number') {
      values.voucherId = Number(values.voucherId);
    }
    if (values.orderId !== undefined && typeof values.orderId !== 'number') {
      values.orderId = Number(values.orderId);
    }
    if (values.customerId !== undefined && typeof values.customerId !== 'number') {
      values.customerId = Number(values.customerId);
    }
    values.redeemedAt = convertDateTimeToServer(values.redeemedAt);

    const entity = {
      ...voucherRedemptionEntity,
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
          redeemedAt: displayDefaultDateTime(),
        }
      : {
          ...voucherRedemptionEntity,
          redeemedAt: convertDateTimeFromServer(voucherRedemptionEntity.redeemedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.voucherRedemption.home.createOrEditLabel" data-cy="VoucherRedemptionCreateUpdateHeading">
            <Translate contentKey="lumiApp.voucherRedemption.home.createOrEditLabel">Create or edit a VoucherRedemption</Translate>
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
                  id="voucher-redemption-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.voucherRedemption.voucherId')}
                id="voucher-redemption-voucherId"
                name="voucherId"
                data-cy="voucherId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.voucherRedemption.orderId')}
                id="voucher-redemption-orderId"
                name="orderId"
                data-cy="orderId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.voucherRedemption.customerId')}
                id="voucher-redemption-customerId"
                name="customerId"
                data-cy="customerId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.voucherRedemption.redeemedAt')}
                id="voucher-redemption-redeemedAt"
                name="redeemedAt"
                data-cy="redeemedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/voucher-redemption" replace color="info">
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

export default VoucherRedemptionUpdate;
