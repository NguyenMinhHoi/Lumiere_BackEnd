import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './sla-plan.reducer';

export const SlaPlanDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const slaPlanEntity = useAppSelector(state => state.slaPlan.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="slaPlanDetailsHeading">
          <Translate contentKey="lumiApp.slaPlan.detail.title">SlaPlan</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{slaPlanEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="lumiApp.slaPlan.name">Name</Translate>
            </span>
          </dt>
          <dd>{slaPlanEntity.name}</dd>
          <dt>
            <span id="firstResponseMins">
              <Translate contentKey="lumiApp.slaPlan.firstResponseMins">First Response Mins</Translate>
            </span>
          </dt>
          <dd>{slaPlanEntity.firstResponseMins}</dd>
          <dt>
            <span id="resolutionMins">
              <Translate contentKey="lumiApp.slaPlan.resolutionMins">Resolution Mins</Translate>
            </span>
          </dt>
          <dd>{slaPlanEntity.resolutionMins}</dd>
          <dt>
            <span id="active">
              <Translate contentKey="lumiApp.slaPlan.active">Active</Translate>
            </span>
          </dt>
          <dd>{slaPlanEntity.active ? 'true' : 'false'}</dd>
        </dl>
        <Button tag={Link} to="/sla-plan" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/sla-plan/${slaPlanEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default SlaPlanDetail;
