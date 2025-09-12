import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { AppType } from 'app/shared/model/enumerations/app-type.model';
import { IntegrationStatus } from 'app/shared/model/enumerations/integration-status.model';
import { createEntity, getEntity, reset, updateEntity } from './integration-log.reducer';

export const IntegrationLogUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const integrationLogEntity = useAppSelector(state => state.integrationLog.entity);
  const loading = useAppSelector(state => state.integrationLog.loading);
  const updating = useAppSelector(state => state.integrationLog.updating);
  const updateSuccess = useAppSelector(state => state.integrationLog.updateSuccess);
  const appTypeValues = Object.keys(AppType);
  const integrationStatusValues = Object.keys(IntegrationStatus);

  const handleClose = () => {
    navigate(`/integration-log${location.search}`);
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
    if (values.retries !== undefined && typeof values.retries !== 'number') {
      values.retries = Number(values.retries);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...integrationLogEntity,
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
          sourceApp: 'SHOPEE',
          targetApp: 'SHOPEE',
          status: 'PENDING',
          ...integrationLogEntity,
          createdAt: convertDateTimeFromServer(integrationLogEntity.createdAt),
          updatedAt: convertDateTimeFromServer(integrationLogEntity.updatedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.integrationLog.home.createOrEditLabel" data-cy="IntegrationLogCreateUpdateHeading">
            <Translate contentKey="lumiApp.integrationLog.home.createOrEditLabel">Create or edit a IntegrationLog</Translate>
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
                  id="integration-log-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.integrationLog.sourceApp')}
                id="integration-log-sourceApp"
                name="sourceApp"
                data-cy="sourceApp"
                type="select"
              >
                {appTypeValues.map(appType => (
                  <option value={appType} key={appType}>
                    {translate(`lumiApp.AppType.${appType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.integrationLog.targetApp')}
                id="integration-log-targetApp"
                name="targetApp"
                data-cy="targetApp"
                type="select"
              >
                {appTypeValues.map(appType => (
                  <option value={appType} key={appType}>
                    {translate(`lumiApp.AppType.${appType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.integrationLog.payload')}
                id="integration-log-payload"
                name="payload"
                data-cy="payload"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.integrationLog.response')}
                id="integration-log-response"
                name="response"
                data-cy="response"
                type="textarea"
              />
              <ValidatedField
                label={translate('lumiApp.integrationLog.status')}
                id="integration-log-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {integrationStatusValues.map(integrationStatus => (
                  <option value={integrationStatus} key={integrationStatus}>
                    {translate(`lumiApp.IntegrationStatus.${integrationStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.integrationLog.retries')}
                id="integration-log-retries"
                name="retries"
                data-cy="retries"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.integrationLog.createdAt')}
                id="integration-log-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.integrationLog.updatedAt')}
                id="integration-log-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/integration-log" replace color="info">
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

export default IntegrationLogUpdate;
