import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { AuditAction } from 'app/shared/model/enumerations/audit-action.model';
import { createEntity, getEntity, reset, updateEntity } from './audit-history.reducer';

export const AuditHistoryUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const auditHistoryEntity = useAppSelector(state => state.auditHistory.entity);
  const loading = useAppSelector(state => state.auditHistory.loading);
  const updating = useAppSelector(state => state.auditHistory.updating);
  const updateSuccess = useAppSelector(state => state.auditHistory.updateSuccess);
  const auditActionValues = Object.keys(AuditAction);

  const handleClose = () => {
    navigate(`/audit-history${location.search}`);
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
    values.performedAt = convertDateTimeToServer(values.performedAt);

    const entity = {
      ...auditHistoryEntity,
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
          performedAt: displayDefaultDateTime(),
        }
      : {
          action: 'CREATE',
          ...auditHistoryEntity,
          performedAt: convertDateTimeFromServer(auditHistoryEntity.performedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.auditHistory.home.createOrEditLabel" data-cy="AuditHistoryCreateUpdateHeading">
            <Translate contentKey="lumiApp.auditHistory.home.createOrEditLabel">Create or edit a AuditHistory</Translate>
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
                  id="audit-history-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.auditHistory.entityName')}
                id="audit-history-entityName"
                name="entityName"
                data-cy="entityName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 128, message: translate('entity.validation.maxlength', { max: 128 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.auditHistory.entityId')}
                id="audit-history-entityId"
                name="entityId"
                data-cy="entityId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.auditHistory.action')}
                id="audit-history-action"
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
                label={translate('lumiApp.auditHistory.oldValue')}
                id="audit-history-oldValue"
                name="oldValue"
                data-cy="oldValue"
                type="textarea"
              />
              <ValidatedField
                label={translate('lumiApp.auditHistory.newValue')}
                id="audit-history-newValue"
                name="newValue"
                data-cy="newValue"
                type="textarea"
              />
              <ValidatedField
                label={translate('lumiApp.auditHistory.performedBy')}
                id="audit-history-performedBy"
                name="performedBy"
                data-cy="performedBy"
                type="text"
                validate={{
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.auditHistory.performedAt')}
                id="audit-history-performedAt"
                name="performedAt"
                data-cy="performedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.auditHistory.ipAddress')}
                id="audit-history-ipAddress"
                name="ipAddress"
                data-cy="ipAddress"
                type="text"
                validate={{
                  maxLength: { value: 45, message: translate('entity.validation.maxlength', { max: 45 }) },
                }}
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/audit-history" replace color="info">
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

export default AuditHistoryUpdate;
