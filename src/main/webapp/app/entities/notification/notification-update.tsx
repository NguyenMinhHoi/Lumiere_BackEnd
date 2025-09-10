import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getTickets } from 'app/entities/ticket/ticket.reducer';
import { getEntities as getCustomers } from 'app/entities/customer/customer.reducer';
import { getEntities as getSurveys } from 'app/entities/survey/survey.reducer';
import { NotificationType } from 'app/shared/model/enumerations/notification-type.model';
import { DeliveryChannel } from 'app/shared/model/enumerations/delivery-channel.model';
import { SendStatus } from 'app/shared/model/enumerations/send-status.model';
import { createEntity, getEntity, reset, updateEntity } from './notification.reducer';

export const NotificationUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const tickets = useAppSelector(state => state.ticket.entities);
  const customers = useAppSelector(state => state.customer.entities);
  const surveys = useAppSelector(state => state.survey.entities);
  const notificationEntity = useAppSelector(state => state.notification.entity);
  const loading = useAppSelector(state => state.notification.loading);
  const updating = useAppSelector(state => state.notification.updating);
  const updateSuccess = useAppSelector(state => state.notification.updateSuccess);
  const notificationTypeValues = Object.keys(NotificationType);
  const deliveryChannelValues = Object.keys(DeliveryChannel);
  const sendStatusValues = Object.keys(SendStatus);

  const handleClose = () => {
    navigate(`/notification${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getTickets({}));
    dispatch(getCustomers({}));
    dispatch(getSurveys({}));
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
    if (values.retryCount !== undefined && typeof values.retryCount !== 'number') {
      values.retryCount = Number(values.retryCount);
    }
    values.lastTriedAt = convertDateTimeToServer(values.lastTriedAt);
    values.createdAt = convertDateTimeToServer(values.createdAt);

    const entity = {
      ...notificationEntity,
      ...values,
      ticket: tickets.find(it => it.id.toString() === values.ticket?.toString()),
      customer: customers.find(it => it.id.toString() === values.customer?.toString()),
      survey: surveys.find(it => it.id.toString() === values.survey?.toString()),
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
          lastTriedAt: displayDefaultDateTime(),
          createdAt: displayDefaultDateTime(),
        }
      : {
          type: 'TICKET_UPDATE',
          channel: 'EMAIL',
          sendStatus: 'PENDING',
          ...notificationEntity,
          lastTriedAt: convertDateTimeFromServer(notificationEntity.lastTriedAt),
          createdAt: convertDateTimeFromServer(notificationEntity.createdAt),
          ticket: notificationEntity?.ticket?.id,
          customer: notificationEntity?.customer?.id,
          survey: notificationEntity?.survey?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.notification.home.createOrEditLabel" data-cy="NotificationCreateUpdateHeading">
            <Translate contentKey="lumiApp.notification.home.createOrEditLabel">Create or edit a Notification</Translate>
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
                  id="notification-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.notification.type')}
                id="notification-type"
                name="type"
                data-cy="type"
                type="select"
              >
                {notificationTypeValues.map(notificationType => (
                  <option value={notificationType} key={notificationType}>
                    {translate(`lumiApp.NotificationType.${notificationType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.notification.channel')}
                id="notification-channel"
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
                label={translate('lumiApp.notification.subject')}
                id="notification-subject"
                name="subject"
                data-cy="subject"
                type="text"
                validate={{
                  maxLength: { value: 200, message: translate('entity.validation.maxlength', { max: 200 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.notification.payload')}
                id="notification-payload"
                name="payload"
                data-cy="payload"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.notification.sendStatus')}
                id="notification-sendStatus"
                name="sendStatus"
                data-cy="sendStatus"
                type="select"
              >
                {sendStatusValues.map(sendStatus => (
                  <option value={sendStatus} key={sendStatus}>
                    {translate(`lumiApp.SendStatus.${sendStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.notification.retryCount')}
                id="notification-retryCount"
                name="retryCount"
                data-cy="retryCount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.notification.lastTriedAt')}
                id="notification-lastTriedAt"
                name="lastTriedAt"
                data-cy="lastTriedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.notification.createdAt')}
                id="notification-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="notification-ticket"
                name="ticket"
                data-cy="ticket"
                label={translate('lumiApp.notification.ticket')}
                type="select"
              >
                <option value="" key="0" />
                {tickets
                  ? tickets.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.code}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="notification-customer"
                name="customer"
                data-cy="customer"
                label={translate('lumiApp.notification.customer')}
                type="select"
              >
                <option value="" key="0" />
                {customers
                  ? customers.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.code}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="notification-survey"
                name="survey"
                data-cy="survey"
                label={translate('lumiApp.notification.survey')}
                type="select"
              >
                <option value="" key="0" />
                {surveys
                  ? surveys.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.title}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/notification" replace color="info">
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

export default NotificationUpdate;
