import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getSurveys } from 'app/entities/survey/survey.reducer';
import { getEntities as getCustomers } from 'app/entities/customer/customer.reducer';
import { getEntities as getTickets } from 'app/entities/ticket/ticket.reducer';
import { createEntity, getEntity, reset, updateEntity } from './survey-response.reducer';

export const SurveyResponseUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const surveys = useAppSelector(state => state.survey.entities);
  const customers = useAppSelector(state => state.customer.entities);
  const tickets = useAppSelector(state => state.ticket.entities);
  const surveyResponseEntity = useAppSelector(state => state.surveyResponse.entity);
  const loading = useAppSelector(state => state.surveyResponse.loading);
  const updating = useAppSelector(state => state.surveyResponse.updating);
  const updateSuccess = useAppSelector(state => state.surveyResponse.updateSuccess);

  const handleClose = () => {
    navigate(`/survey-response${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getSurveys({}));
    dispatch(getCustomers({}));
    dispatch(getTickets({}));
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
    values.respondedAt = convertDateTimeToServer(values.respondedAt);
    if (values.score !== undefined && typeof values.score !== 'number') {
      values.score = Number(values.score);
    }

    const entity = {
      ...surveyResponseEntity,
      ...values,
      survey: surveys.find(it => it.id.toString() === values.survey?.toString()),
      customer: customers.find(it => it.id.toString() === values.customer?.toString()),
      ticket: tickets.find(it => it.id.toString() === values.ticket?.toString()),
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
          respondedAt: displayDefaultDateTime(),
        }
      : {
          ...surveyResponseEntity,
          respondedAt: convertDateTimeFromServer(surveyResponseEntity.respondedAt),
          survey: surveyResponseEntity?.survey?.id,
          customer: surveyResponseEntity?.customer?.id,
          ticket: surveyResponseEntity?.ticket?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.surveyResponse.home.createOrEditLabel" data-cy="SurveyResponseCreateUpdateHeading">
            <Translate contentKey="lumiApp.surveyResponse.home.createOrEditLabel">Create or edit a SurveyResponse</Translate>
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
                  id="survey-response-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.surveyResponse.respondedAt')}
                id="survey-response-respondedAt"
                name="respondedAt"
                data-cy="respondedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.surveyResponse.score')}
                id="survey-response-score"
                name="score"
                data-cy="score"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.surveyResponse.comment')}
                id="survey-response-comment"
                name="comment"
                data-cy="comment"
                type="textarea"
              />
              <ValidatedField
                id="survey-response-survey"
                name="survey"
                data-cy="survey"
                label={translate('lumiApp.surveyResponse.survey')}
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
              <ValidatedField
                id="survey-response-customer"
                name="customer"
                data-cy="customer"
                label={translate('lumiApp.surveyResponse.customer')}
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
                id="survey-response-ticket"
                name="ticket"
                data-cy="ticket"
                label={translate('lumiApp.surveyResponse.ticket')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/survey-response" replace color="info">
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

export default SurveyResponseUpdate;
