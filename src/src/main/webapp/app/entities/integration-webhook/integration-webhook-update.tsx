import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './integration-webhook.reducer';

export const IntegrationWebhookUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const integrationWebhookEntity = useAppSelector(state => state.integrationWebhook.entity);
  const loading = useAppSelector(state => state.integrationWebhook.loading);
  const updating = useAppSelector(state => state.integrationWebhook.updating);
  const updateSuccess = useAppSelector(state => state.integrationWebhook.updateSuccess);

  const handleClose = () => {
    navigate('/integration-webhook');
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

    const entity = {
      ...integrationWebhookEntity,
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
      ? {}
      : {
          ...integrationWebhookEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.integrationWebhook.home.createOrEditLabel" data-cy="IntegrationWebhookCreateUpdateHeading">
            <Translate contentKey="lumiApp.integrationWebhook.home.createOrEditLabel">Create or edit a IntegrationWebhook</Translate>
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
                  id="integration-webhook-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.integrationWebhook.name')}
                id="integration-webhook-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 3, message: translate('entity.validation.minlength', { min: 3 }) },
                  maxLength: { value: 128, message: translate('entity.validation.maxlength', { max: 128 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.integrationWebhook.targetUrl')}
                id="integration-webhook-targetUrl"
                name="targetUrl"
                data-cy="targetUrl"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 512, message: translate('entity.validation.maxlength', { max: 512 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.integrationWebhook.secret')}
                id="integration-webhook-secret"
                name="secret"
                data-cy="secret"
                type="text"
                validate={{
                  maxLength: { value: 128, message: translate('entity.validation.maxlength', { max: 128 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.integrationWebhook.isActive')}
                id="integration-webhook-isActive"
                name="isActive"
                data-cy="isActive"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('lumiApp.integrationWebhook.subscribedEvents')}
                id="integration-webhook-subscribedEvents"
                name="subscribedEvents"
                data-cy="subscribedEvents"
                type="text"
                validate={{
                  maxLength: { value: 512, message: translate('entity.validation.maxlength', { max: 512 }) },
                }}
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/integration-webhook" replace color="info">
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

export default IntegrationWebhookUpdate;
