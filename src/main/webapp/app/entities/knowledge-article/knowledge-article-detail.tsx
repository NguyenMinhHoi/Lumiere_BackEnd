import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './knowledge-article.reducer';

export const KnowledgeArticleDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const knowledgeArticleEntity = useAppSelector(state => state.knowledgeArticle.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="knowledgeArticleDetailsHeading">
          <Translate contentKey="lumiApp.knowledgeArticle.detail.title">KnowledgeArticle</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{knowledgeArticleEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="lumiApp.knowledgeArticle.title">Title</Translate>
            </span>
          </dt>
          <dd>{knowledgeArticleEntity.title}</dd>
          <dt>
            <span id="content">
              <Translate contentKey="lumiApp.knowledgeArticle.content">Content</Translate>
            </span>
          </dt>
          <dd>{knowledgeArticleEntity.content}</dd>
          <dt>
            <span id="published">
              <Translate contentKey="lumiApp.knowledgeArticle.published">Published</Translate>
            </span>
          </dt>
          <dd>{knowledgeArticleEntity.published ? 'true' : 'false'}</dd>
          <dt>
            <span id="views">
              <Translate contentKey="lumiApp.knowledgeArticle.views">Views</Translate>
            </span>
          </dt>
          <dd>{knowledgeArticleEntity.views}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.knowledgeArticle.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>
            {knowledgeArticleEntity.updatedAt ? (
              <TextFormat value={knowledgeArticleEntity.updatedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.knowledgeArticle.category">Category</Translate>
          </dt>
          <dd>{knowledgeArticleEntity.category ? knowledgeArticleEntity.category.name : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.knowledgeArticle.tags">Tags</Translate>
          </dt>
          <dd>
            {knowledgeArticleEntity.tags
              ? knowledgeArticleEntity.tags.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.name}</a>
                    {knowledgeArticleEntity.tags && i === knowledgeArticleEntity.tags.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/knowledge-article" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/knowledge-article/${knowledgeArticleEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default KnowledgeArticleDetail;
