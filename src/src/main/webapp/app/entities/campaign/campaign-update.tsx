import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { DeliveryChannel } from 'app/shared/model/enumerations/delivery-channel.model';
import { createEntity, getEntity, reset, updateEntity } from './campaign.reducer';

export const CampaignUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const campaignEntity = useAppSelector(state => state.campaign.entity);
  const loading = useAppSelector(state => state.campaign.loading);
  const updating = useAppSelector(state => state.campaign.updating);
  const updateSuccess = useAppSelector(state => state.campaign.updateSuccess);
  const deliveryChannelValues = Object.keys(DeliveryChannel);

  const handleClose = () => {
    navigate(`/campaign${location.search}`);
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
    if (values.budget !== undefined && typeof values.budget !== 'number') {
      values.budget = Number(values.budget);
    }
    values.startDate = convertDateTimeToServer(values.startDate);
    values.endDate = convertDateTimeToServer(values.endDate);
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...campaignEntity,
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
          startDate: displayDefaultDateTime(),
          endDate: displayDefaultDateTime(),
          createdAt: displayDefaultDateTime(),
          updatedAt: displayDefaultDateTime(),
        }
      : {
          channel: 'EMAIL',
          ...campaignEntity,
          startDate: convertDateTimeFromServer(campaignEntity.startDate),
          endDate: convertDateTimeFromServer(campaignEntity.endDate),
          createdAt: convertDateTimeFromServer(campaignEntity.createdAt),
          updatedAt: convertDateTimeFromServer(campaignEntity.updatedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.campaign.home.createOrEditLabel" data-cy="CampaignCreateUpdateHeading">
            <Translate contentKey="lumiApp.campaign.home.createOrEditLabel">Create or edit a Campaign</Translate>
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
                  id="campaign-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.campaign.name')}
                id="campaign-name"
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
                label={translate('lumiApp.campaign.description')}
                id="campaign-description"
                name="description"
                data-cy="description"
                type="textarea"
              />
              <ValidatedField
                label={translate('lumiApp.campaign.channel')}
                id="campaign-channel"
                name="channel"
                data-cy="channel"
                type="select"
              >
                {deliveryChannelValues.map(deliveryChannel => (
                  <option value={deliveryChannel} key={deliveryChannel}>
                    {translate(`lumiApp.DeliveryChannel.${deliveryChannel}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.campaign.budget')}
                id="campaign-budget"
                name="budget"
                data-cy="budget"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.campaign.startDate')}
                id="campaign-startDate"
                name="startDate"
                data-cy="startDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.campaign.endDate')}
                id="campaign-endDate"
                name="endDate"
                data-cy="endDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.campaign.isActive')}
                id="campaign-isActive"
                name="isActive"
                data-cy="isActive"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('lumiApp.campaign.createdAt')}
                id="campaign-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.campaign.updatedAt')}
                id="campaign-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/campaign" replace color="info">
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

export default CampaignUpdate;
