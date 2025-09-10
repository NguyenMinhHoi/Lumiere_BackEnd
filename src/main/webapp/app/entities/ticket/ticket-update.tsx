import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getCustomers } from 'app/entities/customer/customer.reducer';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntities as getSlaPlans } from 'app/entities/sla-plan/sla-plan.reducer';
import { getEntities as getOrders } from 'app/entities/orders/orders.reducer';
import { getEntities as getTags } from 'app/entities/tag/tag.reducer';
import { TicketStatus } from 'app/shared/model/enumerations/ticket-status.model';
import { Priority } from 'app/shared/model/enumerations/priority.model';
import { ChannelType } from 'app/shared/model/enumerations/channel-type.model';
import { createEntity, getEntity, reset, updateEntity } from './ticket.reducer';

export const TicketUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const customers = useAppSelector(state => state.customer.entities);
  const users = useAppSelector(state => state.userManagement.users);
  const slaPlans = useAppSelector(state => state.slaPlan.entities);
  const orders = useAppSelector(state => state.orders.entities);
  const tags = useAppSelector(state => state.tag.entities);
  const ticketEntity = useAppSelector(state => state.ticket.entity);
  const loading = useAppSelector(state => state.ticket.loading);
  const updating = useAppSelector(state => state.ticket.updating);
  const updateSuccess = useAppSelector(state => state.ticket.updateSuccess);
  const ticketStatusValues = Object.keys(TicketStatus);
  const priorityValues = Object.keys(Priority);
  const channelTypeValues = Object.keys(ChannelType);

  const handleClose = () => {
    navigate(`/ticket${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCustomers({}));
    dispatch(getUsers({}));
    dispatch(getSlaPlans({}));
    dispatch(getOrders({}));
    dispatch(getTags({}));
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
    values.openedAt = convertDateTimeToServer(values.openedAt);
    values.firstResponseAt = convertDateTimeToServer(values.firstResponseAt);
    values.resolvedAt = convertDateTimeToServer(values.resolvedAt);
    values.slaDueAt = convertDateTimeToServer(values.slaDueAt);

    const entity = {
      ...ticketEntity,
      ...values,
      customer: customers.find(it => it.id.toString() === values.customer?.toString()),
      assignee: users.find(it => it.id.toString() === values.assignee?.toString()),
      slaPlan: slaPlans.find(it => it.id.toString() === values.slaPlan?.toString()),
      order: orders.find(it => it.id.toString() === values.order?.toString()),
      tags: mapIdList(values.tags),
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
          openedAt: displayDefaultDateTime(),
          firstResponseAt: displayDefaultDateTime(),
          resolvedAt: displayDefaultDateTime(),
          slaDueAt: displayDefaultDateTime(),
        }
      : {
          status: 'OPEN',
          priority: 'LOW',
          channel: 'WEB',
          ...ticketEntity,
          openedAt: convertDateTimeFromServer(ticketEntity.openedAt),
          firstResponseAt: convertDateTimeFromServer(ticketEntity.firstResponseAt),
          resolvedAt: convertDateTimeFromServer(ticketEntity.resolvedAt),
          slaDueAt: convertDateTimeFromServer(ticketEntity.slaDueAt),
          customer: ticketEntity?.customer?.id,
          assignee: ticketEntity?.assignee?.id,
          slaPlan: ticketEntity?.slaPlan?.id,
          order: ticketEntity?.order?.id,
          tags: ticketEntity?.tags?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.ticket.home.createOrEditLabel" data-cy="TicketCreateUpdateHeading">
            <Translate contentKey="lumiApp.ticket.home.createOrEditLabel">Create or edit a Ticket</Translate>
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
                  id="ticket-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.ticket.code')}
                id="ticket-code"
                name="code"
                data-cy="code"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 6, message: translate('entity.validation.minlength', { min: 6 }) },
                  maxLength: { value: 32, message: translate('entity.validation.maxlength', { max: 32 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.ticket.subject')}
                id="ticket-subject"
                name="subject"
                data-cy="subject"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 3, message: translate('entity.validation.minlength', { min: 3 }) },
                  maxLength: { value: 200, message: translate('entity.validation.maxlength', { max: 200 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.ticket.description')}
                id="ticket-description"
                name="description"
                data-cy="description"
                type="textarea"
              />
              <ValidatedField label={translate('lumiApp.ticket.status')} id="ticket-status" name="status" data-cy="status" type="select">
                {ticketStatusValues.map(ticketStatus => (
                  <option value={ticketStatus} key={ticketStatus}>
                    {translate(`lumiApp.TicketStatus.${ticketStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.ticket.priority')}
                id="ticket-priority"
                name="priority"
                data-cy="priority"
                type="select"
              >
                {priorityValues.map(priority => (
                  <option value={priority} key={priority}>
                    {translate(`lumiApp.Priority.${priority}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.ticket.channel')}
                id="ticket-channel"
                name="channel"
                data-cy="channel"
                type="select"
              >
                {channelTypeValues.map(channelType => (
                  <option value={channelType} key={channelType}>
                    {translate(`lumiApp.ChannelType.${channelType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.ticket.openedAt')}
                id="ticket-openedAt"
                name="openedAt"
                data-cy="openedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.ticket.firstResponseAt')}
                id="ticket-firstResponseAt"
                name="firstResponseAt"
                data-cy="firstResponseAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.ticket.resolvedAt')}
                id="ticket-resolvedAt"
                name="resolvedAt"
                data-cy="resolvedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.ticket.slaDueAt')}
                id="ticket-slaDueAt"
                name="slaDueAt"
                data-cy="slaDueAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="ticket-customer"
                name="customer"
                data-cy="customer"
                label={translate('lumiApp.ticket.customer')}
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
                id="ticket-assignee"
                name="assignee"
                data-cy="assignee"
                label={translate('lumiApp.ticket.assignee')}
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
              <ValidatedField
                id="ticket-slaPlan"
                name="slaPlan"
                data-cy="slaPlan"
                label={translate('lumiApp.ticket.slaPlan')}
                type="select"
              >
                <option value="" key="0" />
                {slaPlans
                  ? slaPlans.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="ticket-order" name="order" data-cy="order" label={translate('lumiApp.ticket.order')} type="select">
                <option value="" key="0" />
                {orders
                  ? orders.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.code}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField label={translate('lumiApp.ticket.tags')} id="ticket-tags" data-cy="tags" type="select" multiple name="tags">
                <option value="" key="0" />
                {tags
                  ? tags.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/ticket" replace color="info">
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

export default TicketUpdate;
