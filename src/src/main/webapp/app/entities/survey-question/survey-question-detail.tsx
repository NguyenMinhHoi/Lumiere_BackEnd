import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './survey-question.reducer';

export const SurveyQuestionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const surveyQuestionEntity = useAppSelector(state => state.surveyQuestion.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="surveyQuestionDetailsHeading">
          <Translate contentKey="lumiApp.surveyQuestion.detail.title">SurveyQuestion</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{surveyQuestionEntity.id}</dd>
          <dt>
            <span id="surveyId">
              <Translate contentKey="lumiApp.surveyQuestion.surveyId">Survey Id</Translate>
            </span>
          </dt>
          <dd>{surveyQuestionEntity.surveyId}</dd>
          <dt>
            <span id="text">
              <Translate contentKey="lumiApp.surveyQuestion.text">Text</Translate>
            </span>
          </dt>
          <dd>{surveyQuestionEntity.text}</dd>
          <dt>
            <span id="questionType">
              <Translate contentKey="lumiApp.surveyQuestion.questionType">Question Type</Translate>
            </span>
          </dt>
          <dd>{surveyQuestionEntity.questionType}</dd>
          <dt>
            <span id="scaleMin">
              <Translate contentKey="lumiApp.surveyQuestion.scaleMin">Scale Min</Translate>
            </span>
          </dt>
          <dd>{surveyQuestionEntity.scaleMin}</dd>
          <dt>
            <span id="scaleMax">
              <Translate contentKey="lumiApp.surveyQuestion.scaleMax">Scale Max</Translate>
            </span>
          </dt>
          <dd>{surveyQuestionEntity.scaleMax}</dd>
          <dt>
            <span id="isNeed">
              <Translate contentKey="lumiApp.surveyQuestion.isNeed">Is Need</Translate>
            </span>
          </dt>
          <dd>{surveyQuestionEntity.isNeed ? 'true' : 'false'}</dd>
          <dt>
            <span id="orderNo">
              <Translate contentKey="lumiApp.surveyQuestion.orderNo">Order No</Translate>
            </span>
          </dt>
          <dd>{surveyQuestionEntity.orderNo}</dd>
        </dl>
        <Button tag={Link} to="/survey-question" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/survey-question/${surveyQuestionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default SurveyQuestionDetail;
