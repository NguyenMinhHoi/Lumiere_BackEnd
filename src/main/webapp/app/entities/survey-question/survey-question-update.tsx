import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { QuestionType } from 'app/shared/model/enumerations/question-type.model';
import { createEntity, getEntity, reset, updateEntity } from './survey-question.reducer';

export const SurveyQuestionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const surveyQuestionEntity = useAppSelector(state => state.surveyQuestion.entity);
  const loading = useAppSelector(state => state.surveyQuestion.loading);
  const updating = useAppSelector(state => state.surveyQuestion.updating);
  const updateSuccess = useAppSelector(state => state.surveyQuestion.updateSuccess);
  const questionTypeValues = Object.keys(QuestionType);

  const handleClose = () => {
    navigate('/survey-question');
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
    if (values.scaleMin !== undefined && typeof values.scaleMin !== 'number') {
      values.scaleMin = Number(values.scaleMin);
    }
    if (values.scaleMax !== undefined && typeof values.scaleMax !== 'number') {
      values.scaleMax = Number(values.scaleMax);
    }
    if (values.orderNo !== undefined && typeof values.orderNo !== 'number') {
      values.orderNo = Number(values.orderNo);
    }

    const entity = {
      ...surveyQuestionEntity,
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
          questionType: 'SCALE',
          ...surveyQuestionEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.surveyQuestion.home.createOrEditLabel" data-cy="SurveyQuestionCreateUpdateHeading">
            <Translate contentKey="lumiApp.surveyQuestion.home.createOrEditLabel">Create or edit a SurveyQuestion</Translate>
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
                  id="survey-question-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.surveyQuestion.surveyId')}
                id="survey-question-surveyId"
                name="surveyId"
                data-cy="surveyId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.surveyQuestion.text')}
                id="survey-question-text"
                name="text"
                data-cy="text"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 3, message: translate('entity.validation.minlength', { min: 3 }) },
                  maxLength: { value: 300, message: translate('entity.validation.maxlength', { max: 300 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.surveyQuestion.questionType')}
                id="survey-question-questionType"
                name="questionType"
                data-cy="questionType"
                type="select"
              >
                {questionTypeValues.map(questionType => (
                  <option value={questionType} key={questionType}>
                    {translate(`lumiApp.QuestionType.${questionType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.surveyQuestion.scaleMin')}
                id="survey-question-scaleMin"
                name="scaleMin"
                data-cy="scaleMin"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.surveyQuestion.scaleMax')}
                id="survey-question-scaleMax"
                name="scaleMax"
                data-cy="scaleMax"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.surveyQuestion.isNeed')}
                id="survey-question-isNeed"
                name="isNeed"
                data-cy="isNeed"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('lumiApp.surveyQuestion.orderNo')}
                id="survey-question-orderNo"
                name="orderNo"
                data-cy="orderNo"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/survey-question" replace color="info">
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

export default SurveyQuestionUpdate;
