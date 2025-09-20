import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { StockMovementReason } from 'app/shared/model/enumerations/stock-movement-reason.model';
import { createEntity, getEntity, reset, updateEntity } from './cloth-stock-movement.reducer';

export const ClothStockMovementUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const clothStockMovementEntity = useAppSelector(state => state.clothStockMovement.entity);
  const loading = useAppSelector(state => state.clothStockMovement.loading);
  const updating = useAppSelector(state => state.clothStockMovement.updating);
  const updateSuccess = useAppSelector(state => state.clothStockMovement.updateSuccess);
  const stockMovementReasonValues = Object.keys(StockMovementReason);

  const handleClose = () => {
    navigate(`/cloth-stock-movement${location.search}`);
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
      ...clothStockMovementEntity,
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
          ...clothStockMovementEntity,
          createdAt: convertDateTimeFromServer(clothStockMovementEntity.createdAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.clothStockMovement.home.createOrEditLabel" data-cy="ClothStockMovementCreateUpdateHeading">
            <Translate contentKey="lumiApp.clothStockMovement.home.createOrEditLabel">Create or edit a ClothStockMovement</Translate>
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
                  id="cloth-stock-movement-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.clothStockMovement.clothId')}
                id="cloth-stock-movement-clothId"
                name="clothId"
                data-cy="clothId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.clothStockMovement.warehouseId')}
                id="cloth-stock-movement-warehouseId"
                name="warehouseId"
                data-cy="warehouseId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.clothStockMovement.delta')}
                id="cloth-stock-movement-delta"
                name="delta"
                data-cy="delta"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.clothStockMovement.reason')}
                id="cloth-stock-movement-reason"
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
                label={translate('lumiApp.clothStockMovement.refOrderId')}
                id="cloth-stock-movement-refOrderId"
                name="refOrderId"
                data-cy="refOrderId"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.clothStockMovement.createdAt')}
                id="cloth-stock-movement-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/cloth-stock-movement" replace color="info">
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

export default ClothStockMovementUpdate;
