import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getProducts } from 'app/entities/product/product.reducer';
import { getEntities as getSuppliers } from 'app/entities/supplier/supplier.reducer';
import { createEntity, getEntity, reset, updateEntity } from './supplement.reducer';

export const SupplementUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const products = useAppSelector(state => state.product.entities);
  const suppliers = useAppSelector(state => state.supplier.entities);
  const supplementEntity = useAppSelector(state => state.supplement.entity);
  const loading = useAppSelector(state => state.supplement.loading);
  const updating = useAppSelector(state => state.supplement.updating);
  const updateSuccess = useAppSelector(state => state.supplement.updateSuccess);

  const handleClose = () => {
    navigate(`/supplement${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getProducts({}));
    dispatch(getSuppliers({}));
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
      ...supplementEntity,
      ...values,
      product: products.find(it => it.id.toString() === values.product?.toString()),
      supplier: suppliers.find(it => it.id.toString() === values.supplier?.toString()),
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
          ...supplementEntity,
          createdAt: convertDateTimeFromServer(supplementEntity.createdAt),
          updatedAt: convertDateTimeFromServer(supplementEntity.updatedAt),
          product: supplementEntity?.product?.id,
          supplier: supplementEntity?.supplier?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.supplement.home.createOrEditLabel" data-cy="SupplementCreateUpdateHeading">
            <Translate contentKey="lumiApp.supplement.home.createOrEditLabel">Create or edit a Supplement</Translate>
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
                  id="supplement-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.supplement.supplyPrice')}
                id="supplement-supplyPrice"
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
                label={translate('lumiApp.supplement.currency')}
                id="supplement-currency"
                name="currency"
                data-cy="currency"
                type="text"
                validate={{
                  maxLength: { value: 3, message: translate('entity.validation.maxlength', { max: 3 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.supplement.leadTimeDays')}
                id="supplement-leadTimeDays"
                name="leadTimeDays"
                data-cy="leadTimeDays"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.supplement.minOrderQty')}
                id="supplement-minOrderQty"
                name="minOrderQty"
                data-cy="minOrderQty"
                type="text"
                validate={{
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.supplement.isPreferred')}
                id="supplement-isPreferred"
                name="isPreferred"
                data-cy="isPreferred"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('lumiApp.supplement.createdAt')}
                id="supplement-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.supplement.updatedAt')}
                id="supplement-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="supplement-product"
                name="product"
                data-cy="product"
                label={translate('lumiApp.supplement.product')}
                type="select"
              >
                <option value="" key="0" />
                {products
                  ? products.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.code}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="supplement-supplier"
                name="supplier"
                data-cy="supplier"
                label={translate('lumiApp.supplement.supplier')}
                type="select"
              >
                <option value="" key="0" />
                {suppliers
                  ? suppliers.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.code}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/supplement" replace color="info">
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

export default SupplementUpdate;
