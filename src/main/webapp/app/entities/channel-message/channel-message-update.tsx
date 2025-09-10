import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getTickets } from 'app/entities/ticket/ticket.reducer';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { MessageDirection } from 'app/shared/model/enumerations/message-direction.model';
import { createEntity, getEntity, reset, updateEntity } from './channel-message.reducer';

export const ChannelMessageUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const tickets = useAppSelector(state => state.ticket.entities);
  const users = useAppSelector(state => state.userManagement.users);
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

    dispatch(getTickets({}));
    dispatch(getUsers({}));
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
    values.sentAt = convertDateTimeToServer(values.sentAt);

    const entity = {
      ...channelMessageEntity,
      ...values,
      ticket: tickets.find(it => it.id.toString() === values.ticket?.toString()),
      author: users.find(it => it.id.toString() === values.author?.toString()),
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
          ticket: channelMessageEntity?.ticket?.id,
          author: channelMessageEntity?.author?.id,
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
              <ValidatedField
                id="channel-message-ticket"
                name="ticket"
                data-cy="ticket"
                label={translate('lumiApp.channelMessage.ticket')}
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
                id="channel-message-author"
                name="author"
                data-cy="author"
                label={translate('lumiApp.channelMessage.author')}
                type="select"
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
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
