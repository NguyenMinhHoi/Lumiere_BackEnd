import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './article-tag.reducer';

export const ArticleTagDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const articleTagEntity = useAppSelector(state => state.articleTag.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="articleTagDetailsHeading">
          <Translate contentKey="lumiApp.articleTag.detail.title">ArticleTag</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{articleTagEntity.id}</dd>
          <dt>
            <span id="articleId">
              <Translate contentKey="lumiApp.articleTag.articleId">Article Id</Translate>
            </span>
          </dt>
          <dd>{articleTagEntity.articleId}</dd>
          <dt>
            <span id="tagId">
              <Translate contentKey="lumiApp.articleTag.tagId">Tag Id</Translate>
            </span>
          </dt>
          <dd>{articleTagEntity.tagId}</dd>
        </dl>
        <Button tag={Link} to="/article-tag" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/article-tag/${articleTagEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ArticleTagDetail;
