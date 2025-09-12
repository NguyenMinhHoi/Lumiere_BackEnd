import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { VoucherType } from 'app/shared/model/enumerations/voucher-type.model';
import { VoucherStatus } from 'app/shared/model/enumerations/voucher-status.model';
import { createEntity, getEntity, reset, updateEntity } from './voucher.reducer';

export const VoucherUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const voucherEntity = useAppSelector(state => state.voucher.entity);
  const loading = useAppSelector(state => state.voucher.loading);
  const updating = useAppSelector(state => state.voucher.updating);
  const updateSuccess = useAppSelector(state => state.voucher.updateSuccess);
  const voucherTypeValues = Object.keys(VoucherType);
  const voucherStatusValues = Object.keys(VoucherStatus);

  const handleClose = () => {
    navigate(`/voucher${location.search}`);
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
    if (values.discountValue !== undefined && typeof values.discountValue !== 'number') {
      values.discountValue = Number(values.discountValue);
    }
    if (values.minOrderValue !== undefined && typeof values.minOrderValue !== 'number') {
      values.minOrderValue = Number(values.minOrderValue);
    }
    if (values.maxDiscountValue !== undefined && typeof values.maxDiscountValue !== 'number') {
      values.maxDiscountValue = Number(values.maxDiscountValue);
    }
    if (values.usageLimit !== undefined && typeof values.usageLimit !== 'number') {
      values.usageLimit = Number(values.usageLimit);
    }
    if (values.usedCount !== undefined && typeof values.usedCount !== 'number') {
      values.usedCount = Number(values.usedCount);
    }
    values.validFrom = convertDateTimeToServer(values.validFrom);
    values.validTo = convertDateTimeToServer(values.validTo);
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...voucherEntity,
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
          validFrom: displayDefaultDateTime(),
          validTo: displayDefaultDateTime(),
          createdAt: displayDefaultDateTime(),
          updatedAt: displayDefaultDateTime(),
        }
      : {
          discountType: 'PERCENT',
          status: 'ACTIVE',
          ...voucherEntity,
          validFrom: convertDateTimeFromServer(voucherEntity.validFrom),
          validTo: convertDateTimeFromServer(voucherEntity.validTo),
          createdAt: convertDateTimeFromServer(voucherEntity.createdAt),
          updatedAt: convertDateTimeFromServer(voucherEntity.updatedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.voucher.home.createOrEditLabel" data-cy="VoucherCreateUpdateHeading">
            <Translate contentKey="lumiApp.voucher.home.createOrEditLabel">Create or edit a Voucher</Translate>
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
                  id="voucher-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.voucher.code')}
                id="voucher-code"
                name="code"
                data-cy="code"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 3, message: translate('entity.validation.minlength', { min: 3 }) },
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.voucher.discountType')}
                id="voucher-discountType"
                name="discountType"
                data-cy="discountType"
                type="select"
              >
                {voucherTypeValues.map(voucherType => (
                  <option value={voucherType} key={voucherType}>
                    {translate(`lumiApp.VoucherType.${voucherType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.voucher.discountValue')}
                id="voucher-discountValue"
                name="discountValue"
                data-cy="discountValue"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.voucher.minOrderValue')}
                id="voucher-minOrderValue"
                name="minOrderValue"
                data-cy="minOrderValue"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.voucher.maxDiscountValue')}
                id="voucher-maxDiscountValue"
                name="maxDiscountValue"
                data-cy="maxDiscountValue"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.voucher.usageLimit')}
                id="voucher-usageLimit"
                name="usageLimit"
                data-cy="usageLimit"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.voucher.usedCount')}
                id="voucher-usedCount"
                name="usedCount"
                data-cy="usedCount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.voucher.validFrom')}
                id="voucher-validFrom"
                name="validFrom"
                data-cy="validFrom"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.voucher.validTo')}
                id="voucher-validTo"
                name="validTo"
                data-cy="validTo"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField label={translate('lumiApp.voucher.status')} id="voucher-status" name="status" data-cy="status" type="select">
                {voucherStatusValues.map(voucherStatus => (
                  <option value={voucherStatus} key={voucherStatus}>
                    {translate(`lumiApp.VoucherStatus.${voucherStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.voucher.createdAt')}
                id="voucher-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.voucher.updatedAt')}
                id="voucher-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/voucher" replace color="info">
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

export default VoucherUpdate;
