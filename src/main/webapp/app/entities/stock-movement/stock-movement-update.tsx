import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { StockMovementReason } from 'app/shared/model/enumerations/stock-movement-reason.model';
import { createEntity, getEntity, reset, updateEntity } from './stock-movement.reducer';

export const StockMovementUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const stockMovementEntity = useAppSelector(state => state.stockMovement.entity);
  const loading = useAppSelector(state => state.stockMovement.loading);
  const updating = useAppSelector(state => state.stockMovement.updating);
  const updateSuccess = useAppSelector(state => state.stockMovement.updateSuccess);
  const stockMovementReasonValues = Object.keys(StockMovementReason);

  const handleClose = () => {
    navigate(`/stock-movement${location.search}`);
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
    if (values.productVariantId !== undefined && typeof values.productVariantId !== 'number') {
      values.productVariantId = Number(values.productVariantId);
    }
    if (values.warehouseId !== undefined && typeof values.warehouseId !== 'number') {
      values.warehouseId = Number(values.warehouseId);
    }
    if (values.delta !== undefined && typeof values.delta !== 'number') {
      values.delta = Number(values.delta);
    }
    if (values.refOrderId !== undefined && typeof values.refOrderId !== 'number') {
      values.refOrderId = Number(values.refOrderId);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);

    const entity = {
      ...stockMovementEntity,
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
          reason: 'PURCHASE',
          ...stockMovementEntity,
          createdAt: convertDateTimeFromServer(stockMovementEntity.createdAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.stockMovement.home.createOrEditLabel" data-cy="StockMovementCreateUpdateHeading">
            <Translate contentKey="lumiApp.stockMovement.home.createOrEditLabel">Create or edit a StockMovement</Translate>
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
                  id="stock-movement-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.stockMovement.productVariantId')}
                id="stock-movement-productVariantId"
                name="productVariantId"
                data-cy="productVariantId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.stockMovement.warehouseId')}
                id="stock-movement-warehouseId"
                name="warehouseId"
                data-cy="warehouseId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.stockMovement.delta')}
                id="stock-movement-delta"
                name="delta"
                data-cy="delta"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.stockMovement.reason')}
                id="stock-movement-reason"
                name="reason"
                data-cy="reason"
                type="select"
              >
                {stockMovementReasonValues.map(stockMovementReason => (
                  <option value={stockMovementReason} key={stockMovementReason}>
                    {translate(`lumiApp.StockMovementReason.${stockMovementReason}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.stockMovement.refOrderId')}
                id="stock-movement-refOrderId"
                name="refOrderId"
                data-cy="refOrderId"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.stockMovement.createdAt')}
                id="stock-movement-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/stock-movement" replace color="info">
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

export default StockMovementUpdate;
