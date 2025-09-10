import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './survey-response.reducer';

export const SurveyResponseDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const surveyResponseEntity = useAppSelector(state => state.surveyResponse.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="surveyResponseDetailsHeading">
          <Translate contentKey="lumiApp.surveyResponse.detail.title">SurveyResponse</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{surveyResponseEntity.id}</dd>
          <dt>
            <span id="respondedAt">
              <Translate contentKey="lumiApp.surveyResponse.respondedAt">Responded At</Translate>
            </span>
          </dt>
          <dd>
            {surveyResponseEntity.respondedAt ? (
              <TextFormat value={surveyResponseEntity.respondedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="score">
              <Translate contentKey="lumiApp.surveyResponse.score">Score</Translate>
            </span>
          </dt>
          <dd>{surveyResponseEntity.score}</dd>
          <dt>
            <span id="comment">
              <Translate contentKey="lumiApp.surveyResponse.comment">Comment</Translate>
            </span>
          </dt>
          <dd>{surveyResponseEntity.comment}</dd>
          <dt>
            <Translate contentKey="lumiApp.surveyResponse.survey">Survey</Translate>
          </dt>
          <dd>{surveyResponseEntity.survey ? surveyResponseEntity.survey.title : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.surveyResponse.customer">Customer</Translate>
          </dt>
          <dd>{surveyResponseEntity.customer ? surveyResponseEntity.customer.code : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.surveyResponse.ticket">Ticket</Translate>
          </dt>
          <dd>{surveyResponseEntity.ticket ? surveyResponseEntity.ticket.code : ''}</dd>
        </dl>
        <Button tag={Link} to="/survey-response" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/survey-response/${surveyResponseEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default SurveyResponseDetail;
