import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './product-variant.reducer';

export const ProductVariantUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const productVariantEntity = useAppSelector(state => state.productVariant.entity);
  const loading = useAppSelector(state => state.productVariant.loading);
  const updating = useAppSelector(state => state.productVariant.updating);
  const updateSuccess = useAppSelector(state => state.productVariant.updateSuccess);

  const handleClose = () => {
    navigate(`/product-variant${location.search}`);
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
    if (values.productId !== undefined && typeof values.productId !== 'number') {
      values.productId = Number(values.productId);
    }
    if (values.price !== undefined && typeof values.price !== 'number') {
      values.price = Number(values.price);
    }
    if (values.compareAtPrice !== undefined && typeof values.compareAtPrice !== 'number') {
      values.compareAtPrice = Number(values.compareAtPrice);
    }
    if (values.stockQuantity !== undefined && typeof values.stockQuantity !== 'number') {
      values.stockQuantity = Number(values.stockQuantity);
    }
    if (values.weight !== undefined && typeof values.weight !== 'number') {
      values.weight = Number(values.weight);
    }
    if (values.length !== undefined && typeof values.length !== 'number') {
      values.length = Number(values.length);
    }
    if (values.width !== undefined && typeof values.width !== 'number') {
      values.width = Number(values.width);
    }
    if (values.height !== undefined && typeof values.height !== 'number') {
      values.height = Number(values.height);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...productVariantEntity,
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
          ...productVariantEntity,
          createdAt: convertDateTimeFromServer(productVariantEntity.createdAt),
          updatedAt: convertDateTimeFromServer(productVariantEntity.updatedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.productVariant.home.createOrEditLabel" data-cy="ProductVariantCreateUpdateHeading">
            <Translate contentKey="lumiApp.productVariant.home.createOrEditLabel">Create or edit a ProductVariant</Translate>
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
                  id="product-variant-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.productVariant.productId')}
                id="product-variant-productId"
                name="productId"
                data-cy="productId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.productVariant.sku')}
                id="product-variant-sku"
                name="sku"
                data-cy="sku"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 3, message: translate('entity.validation.minlength', { min: 3 }) },
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.productVariant.name')}
                id="product-variant-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 2, message: translate('entity.validation.minlength', { min: 2 }) },
                  maxLength: { value: 200, message: translate('entity.validation.maxlength', { max: 200 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.productVariant.price')}
                id="product-variant-price"
                name="price"
                data-cy="price"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.productVariant.compareAtPrice')}
                id="product-variant-compareAtPrice"
                name="compareAtPrice"
                data-cy="compareAtPrice"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.productVariant.currency')}
                id="product-variant-currency"
                name="currency"
                data-cy="currency"
                type="text"
                validate={{
                  maxLength: { value: 3, message: translate('entity.validation.maxlength', { max: 3 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.productVariant.stockQuantity')}
                id="product-variant-stockQuantity"
                name="stockQuantity"
                data-cy="stockQuantity"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.productVariant.weight')}
                id="product-variant-weight"
                name="weight"
                data-cy="weight"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.productVariant.length')}
                id="product-variant-length"
                name="length"
                data-cy="length"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.productVariant.width')}
                id="product-variant-width"
                name="width"
                data-cy="width"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.productVariant.height')}
                id="product-variant-height"
                name="height"
                data-cy="height"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.productVariant.isDefault')}
                id="product-variant-isDefault"
                name="isDefault"
                data-cy="isDefault"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('lumiApp.productVariant.createdAt')}
                id="product-variant-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.productVariant.updatedAt')}
                id="product-variant-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/product-variant" replace color="info">
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

export default ProductVariantUpdate;
