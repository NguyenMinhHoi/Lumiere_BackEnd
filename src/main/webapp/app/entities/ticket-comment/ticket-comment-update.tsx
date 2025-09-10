import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getTickets } from 'app/entities/ticket/ticket.reducer';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { Visibility } from 'app/shared/model/enumerations/visibility.model';
import { createEntity, getEntity, reset, updateEntity } from './ticket-comment.reducer';

export const TicketCommentUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const tickets = useAppSelector(state => state.ticket.entities);
  const users = useAppSelector(state => state.userManagement.users);
  const ticketCommentEntity = useAppSelector(state => state.ticketComment.entity);
  const loading = useAppSelector(state => state.ticketComment.loading);
  const updating = useAppSelector(state => state.ticketComment.updating);
  const updateSuccess = useAppSelector(state => state.ticketComment.updateSuccess);
  const visibilityValues = Object.keys(Visibility);

  const handleClose = () => {
    navigate(`/ticket-comment${location.search}`);
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
    values.createdAt = convertDateTimeToServer(values.createdAt);

    const entity = {
      ...ticketCommentEntity,
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
          createdAt: displayDefaultDateTime(),
        }
      : {
          visibility: 'PUBLIC',
          ...ticketCommentEntity,
          createdAt: convertDateTimeFromServer(ticketCommentEntity.createdAt),
          ticket: ticketCommentEntity?.ticket?.id,
          author: ticketCommentEntity?.author?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.ticketComment.home.createOrEditLabel" data-cy="TicketCommentCreateUpdateHeading">
            <Translate contentKey="lumiApp.ticketComment.home.createOrEditLabel">Create or edit a TicketComment</Translate>
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
                  id="ticket-comment-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.ticketComment.body')}
                id="ticket-comment-body"
                name="body"
                data-cy="body"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.ticketComment.visibility')}
                id="ticket-comment-visibility"
                name="visibility"
                data-cy="visibility"
                type="select"
              >
                {visibilityValues.map(visibility => (
                  <option value={visibility} key={visibility}>
                    {translate(`lumiApp.Visibility.${visibility}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.ticketComment.createdAt')}
                id="ticket-comment-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="ticket-comment-ticket"
                name="ticket"
                data-cy="ticket"
                label={translate('lumiApp.ticketComment.ticket')}
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
                id="ticket-comment-author"
                name="author"
                data-cy="author"
                label={translate('lumiApp.ticketComment.author')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/ticket-comment" replace color="info">
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

export default TicketCommentUpdate;
