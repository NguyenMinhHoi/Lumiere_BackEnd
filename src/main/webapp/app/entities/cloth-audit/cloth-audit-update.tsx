import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { AuditAction } from 'app/shared/model/enumerations/audit-action.model';
import { createEntity, getEntity, reset, updateEntity } from './cloth-audit.reducer';

export const ClothAuditUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const clothAuditEntity = useAppSelector(state => state.clothAudit.entity);
  const loading = useAppSelector(state => state.clothAudit.loading);
  const updating = useAppSelector(state => state.clothAudit.updating);
  const updateSuccess = useAppSelector(state => state.clothAudit.updateSuccess);
  const auditActionValues = Object.keys(AuditAction);

  const handleClose = () => {
    navigate(`/cloth-audit${location.search}`);
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
    if (values.productId !== undefined && typeof values.productId !== 'number') {
      values.productId = Number(values.productId);
    }
    if (values.quantity !== undefined && typeof values.quantity !== 'number') {
      values.quantity = Number(values.quantity);
    }
    values.sentAt = convertDateTimeToServer(values.sentAt);
    values.createdAt = convertDateTimeToServer(values.createdAt);

    const entity = {
      ...clothAuditEntity,
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
          sentAt: displayDefaultDateTime(),
          createdAt: displayDefaultDateTime(),
        }
      : {
          action: 'SEND',
          ...clothAuditEntity,
          sentAt: convertDateTimeFromServer(clothAuditEntity.sentAt),
          createdAt: convertDateTimeFromServer(clothAuditEntity.createdAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.clothAudit.home.createOrEditLabel" data-cy="ClothAuditCreateUpdateHeading">
            <Translate contentKey="lumiApp.clothAudit.home.createOrEditLabel">Create or edit a ClothAudit</Translate>
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
                  id="cloth-audit-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.clothAudit.clothId')}
                id="cloth-audit-clothId"
                name="clothId"
                data-cy="clothId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.clothAudit.supplierId')}
                id="cloth-audit-supplierId"
                name="supplierId"
                data-cy="supplierId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.clothAudit.productId')}
                id="cloth-audit-productId"
                name="productId"
                data-cy="productId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.clothAudit.action')}
                id="cloth-audit-action"
                name="action"
                data-cy="action"
                type="select"
              >
                {auditActionValues.map(auditAction => (
                  <option value={auditAction} key={auditAction}>
                    {translate(`lumiApp.AuditAction.${auditAction}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.clothAudit.quantity')}
                id="cloth-audit-quantity"
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
                label={translate('lumiApp.clothAudit.unit')}
                id="cloth-audit-unit"
                name="unit"
                data-cy="unit"
                type="text"
                validate={{
                  maxLength: { value: 16, message: translate('entity.validation.maxlength', { max: 16 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.clothAudit.sentAt')}
                id="cloth-audit-sentAt"
                name="sentAt"
                data-cy="sentAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.clothAudit.note')}
                id="cloth-audit-note"
                name="note"
                data-cy="note"
                type="text"
                validate={{
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.clothAudit.createdAt')}
                id="cloth-audit-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/cloth-audit" replace color="info">
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

export default ClothAuditUpdate;
