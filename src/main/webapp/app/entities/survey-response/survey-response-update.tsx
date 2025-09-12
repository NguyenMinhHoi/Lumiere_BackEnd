import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './survey-response.reducer';

export const SurveyResponseUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

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
    if (values.surveyId !== undefined && typeof values.surveyId !== 'number') {
      values.surveyId = Number(values.surveyId);
    }
    if (values.customerId !== undefined && typeof values.customerId !== 'number') {
      values.customerId = Number(values.customerId);
    }
    if (values.ticketId !== undefined && typeof values.ticketId !== 'number') {
      values.ticketId = Number(values.ticketId);
    }
    values.respondedAt = convertDateTimeToServer(values.respondedAt);
    if (values.score !== undefined && typeof values.score !== 'number') {
      values.score = Number(values.score);
    }

    const entity = {
      ...surveyResponseEntity,
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
          respondedAt: displayDefaultDateTime(),
        }
      : {
          ...surveyResponseEntity,
          respondedAt: convertDateTimeFromServer(surveyResponseEntity.respondedAt),
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
                label={translate('lumiApp.surveyResponse.surveyId')}
                id="survey-response-surveyId"
                name="surveyId"
                data-cy="surveyId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.surveyResponse.customerId')}
                id="survey-response-customerId"
                name="customerId"
                data-cy="customerId"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.surveyResponse.ticketId')}
                id="survey-response-ticketId"
                name="ticketId"
                data-cy="ticketId"
                type="text"
              />
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
