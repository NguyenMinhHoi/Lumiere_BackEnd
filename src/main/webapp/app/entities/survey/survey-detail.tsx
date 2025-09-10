import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './survey.reducer';

export const SurveyDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const surveyEntity = useAppSelector(state => state.survey.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="surveyDetailsHeading">
          <Translate contentKey="lumiApp.survey.detail.title">Survey</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{surveyEntity.id}</dd>
          <dt>
            <span id="surveyType">
              <Translate contentKey="lumiApp.survey.surveyType">Survey Type</Translate>
            </span>
          </dt>
          <dd>{surveyEntity.surveyType}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="lumiApp.survey.title">Title</Translate>
            </span>
          </dt>
          <dd>{surveyEntity.title}</dd>
          <dt>
            <span id="sentAt">
              <Translate contentKey="lumiApp.survey.sentAt">Sent At</Translate>
            </span>
          </dt>
          <dd>{surveyEntity.sentAt ? <TextFormat value={surveyEntity.sentAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="dueAt">
              <Translate contentKey="lumiApp.survey.dueAt">Due At</Translate>
            </span>
          </dt>
          <dd>{surveyEntity.dueAt ? <TextFormat value={surveyEntity.dueAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="isActive">
              <Translate contentKey="lumiApp.survey.isActive">Is Active</Translate>
            </span>
          </dt>
          <dd>{surveyEntity.isActive ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="lumiApp.survey.customer">Customer</Translate>
          </dt>
          <dd>{surveyEntity.customer ? surveyEntity.customer.code : ''}</dd>
        </dl>
        <Button tag={Link} to="/survey" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/survey/${surveyEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default SurveyDetail;
