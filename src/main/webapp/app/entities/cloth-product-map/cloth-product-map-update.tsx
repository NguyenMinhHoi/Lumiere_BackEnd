import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './cloth-product-map.reducer';

export const ClothProductMapUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const clothProductMapEntity = useAppSelector(state => state.clothProductMap.entity);
  const loading = useAppSelector(state => state.clothProductMap.loading);
  const updating = useAppSelector(state => state.clothProductMap.updating);
  const updateSuccess = useAppSelector(state => state.clothProductMap.updateSuccess);

  const handleClose = () => {
    navigate(`/cloth-product-map${location.search}`);
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
    if (values.productId !== undefined && typeof values.productId !== 'number') {
      values.productId = Number(values.productId);
    }
    if (values.quantity !== undefined && typeof values.quantity !== 'number') {
      values.quantity = Number(values.quantity);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);

    const entity = {
      ...clothProductMapEntity,
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
        }
      : {
          ...clothProductMapEntity,
          createdAt: convertDateTimeFromServer(clothProductMapEntity.createdAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.clothProductMap.home.createOrEditLabel" data-cy="ClothProductMapCreateUpdateHeading">
            <Translate contentKey="lumiApp.clothProductMap.home.createOrEditLabel">Create or edit a ClothProductMap</Translate>
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
                  id="cloth-product-map-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.clothProductMap.clothId')}
                id="cloth-product-map-clothId"
                name="clothId"
                data-cy="clothId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.clothProductMap.productId')}
                id="cloth-product-map-productId"
                name="productId"
                data-cy="productId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.clothProductMap.quantity')}
                id="cloth-product-map-quantity"
                name="quantity"
                data-cy="quantity"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.clothProductMap.unit')}
                id="cloth-product-map-unit"
                name="unit"
                data-cy="unit"
                type="text"
                validate={{
                  maxLength: { value: 16, message: translate('entity.validation.maxlength', { max: 16 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.clothProductMap.note')}
                id="cloth-product-map-note"
                name="note"
                data-cy="note"
                type="text"
                validate={{
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.clothProductMap.createdAt')}
                id="cloth-product-map-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/cloth-product-map" replace color="info">
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

export default ClothProductMapUpdate;
