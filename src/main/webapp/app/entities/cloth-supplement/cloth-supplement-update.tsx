import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './cloth-supplement.reducer';

export const ClothSupplementUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const clothSupplementEntity = useAppSelector(state => state.clothSupplement.entity);
  const loading = useAppSelector(state => state.clothSupplement.loading);
  const updating = useAppSelector(state => state.clothSupplement.updating);
  const updateSuccess = useAppSelector(state => state.clothSupplement.updateSuccess);

  const handleClose = () => {
    navigate(`/cloth-supplement${location.search}`);
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
    if (values.clothId !== undefined && typeof values.clothId !== 'number') {
      values.clothId = Number(values.clothId);
    }
    if (values.supplierId !== undefined && typeof values.supplierId !== 'number') {
      values.supplierId = Number(values.supplierId);
    }
    if (values.supplyPrice !== undefined && typeof values.supplyPrice !== 'number') {
      values.supplyPrice = Number(values.supplyPrice);
    }
    if (values.leadTimeDays !== undefined && typeof values.leadTimeDays !== 'number') {
      values.leadTimeDays = Number(values.leadTimeDays);
    }
    if (values.minOrderQty !== undefined && typeof values.minOrderQty !== 'number') {
      values.minOrderQty = Number(values.minOrderQty);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...clothSupplementEntity,
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
          createdAt: displayDefaultDateTime(),
          updatedAt: displayDefaultDateTime(),
        }
      : {
          ...clothSupplementEntity,
          createdAt: convertDateTimeFromServer(clothSupplementEntity.createdAt),
          updatedAt: convertDateTimeFromServer(clothSupplementEntity.updatedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.clothSupplement.home.createOrEditLabel" data-cy="ClothSupplementCreateUpdateHeading">
            <Translate contentKey="lumiApp.clothSupplement.home.createOrEditLabel">Create or edit a ClothSupplement</Translate>
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
                  id="cloth-supplement-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.clothSupplement.clothId')}
                id="cloth-supplement-clothId"
                name="clothId"
                data-cy="clothId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.clothSupplement.supplierId')}
                id="cloth-supplement-supplierId"
                name="supplierId"
                data-cy="supplierId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.clothSupplement.supplyPrice')}
                id="cloth-supplement-supplyPrice"
                name="supplyPrice"
                data-cy="supplyPrice"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.clothSupplement.currency')}
                id="cloth-supplement-currency"
                name="currency"
                data-cy="currency"
                type="text"
                validate={{
                  maxLength: { value: 3, message: translate('entity.validation.maxlength', { max: 3 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.clothSupplement.leadTimeDays')}
                id="cloth-supplement-leadTimeDays"
                name="leadTimeDays"
                data-cy="leadTimeDays"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.clothSupplement.minOrderQty')}
                id="cloth-supplement-minOrderQty"
                name="minOrderQty"
                data-cy="minOrderQty"
                type="text"
                validate={{
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.clothSupplement.isPreferred')}
                id="cloth-supplement-isPreferred"
                name="isPreferred"
                data-cy="isPreferred"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('lumiApp.clothSupplement.createdAt')}
                id="cloth-supplement-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.clothSupplement.updatedAt')}
                id="cloth-supplement-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/cloth-supplement" replace color="info">
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

export default ClothSupplementUpdate;
