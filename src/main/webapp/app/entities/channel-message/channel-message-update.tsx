import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { MessageDirection } from 'app/shared/model/enumerations/message-direction.model';
import { createEntity, getEntity, reset, updateEntity } from './channel-message.reducer';

export const ChannelMessageUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const channelMessageEntity = useAppSelector(state => state.channelMessage.entity);
  const loading = useAppSelector(state => state.channelMessage.loading);
  const updating = useAppSelector(state => state.channelMessage.updating);
  const updateSuccess = useAppSelector(state => state.channelMessage.updateSuccess);
  const messageDirectionValues = Object.keys(MessageDirection);

  const handleClose = () => {
    navigate(`/channel-message${location.search}`);
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
    if (values.ticketId !== undefined && typeof values.ticketId !== 'number') {
      values.ticketId = Number(values.ticketId);
    }
    if (values.authorId !== undefined && typeof values.authorId !== 'number') {
      values.authorId = Number(values.authorId);
    }
    values.sentAt = convertDateTimeToServer(values.sentAt);

    const entity = {
      ...channelMessageEntity,
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
        }
      : {
          direction: 'INBOUND',
          ...channelMessageEntity,
          sentAt: convertDateTimeFromServer(channelMessageEntity.sentAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.channelMessage.home.createOrEditLabel" data-cy="ChannelMessageCreateUpdateHeading">
            <Translate contentKey="lumiApp.channelMessage.home.createOrEditLabel">Create or edit a ChannelMessage</Translate>
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
                  id="channel-message-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.channelMessage.ticketId')}
                id="channel-message-ticketId"
                name="ticketId"
                data-cy="ticketId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.channelMessage.authorId')}
                id="channel-message-authorId"
                name="authorId"
                data-cy="authorId"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.channelMessage.direction')}
                id="channel-message-direction"
                name="direction"
                data-cy="direction"
                type="select"
              >
                {messageDirectionValues.map(messageDirection => (
                  <option value={messageDirection} key={messageDirection}>
                    {translate(`lumiApp.MessageDirection.${messageDirection}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.channelMessage.content')}
                id="channel-message-content"
                name="content"
                data-cy="content"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.channelMessage.sentAt')}
                id="channel-message-sentAt"
                name="sentAt"
                data-cy="sentAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.channelMessage.externalMessageId')}
                id="channel-message-externalMessageId"
                name="externalMessageId"
                data-cy="externalMessageId"
                type="text"
                validate={{
                  maxLength: { value: 128, message: translate('entity.validation.maxlength', { max: 128 }) },
                }}
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/channel-message" replace color="info">
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

export default ChannelMessageUpdate;
