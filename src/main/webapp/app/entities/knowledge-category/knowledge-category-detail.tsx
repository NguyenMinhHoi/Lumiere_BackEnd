import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './knowledge-category.reducer';

export const KnowledgeCategoryDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const knowledgeCategoryEntity = useAppSelector(state => state.knowledgeCategory.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="knowledgeCategoryDetailsHeading">
          <Translate contentKey="lumiApp.knowledgeCategory.detail.title">KnowledgeCategory</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{knowledgeCategoryEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="lumiApp.knowledgeCategory.name">Name</Translate>
            </span>
          </dt>
          <dd>{knowledgeCategoryEntity.name}</dd>
          <dt>
            <span id="slug">
              <Translate contentKey="lumiApp.knowledgeCategory.slug">Slug</Translate>
            </span>
          </dt>
          <dd>{knowledgeCategoryEntity.slug}</dd>
        </dl>
        <Button tag={Link} to="/knowledge-category" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/knowledge-category/${knowledgeCategoryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default KnowledgeCategoryDetail;
