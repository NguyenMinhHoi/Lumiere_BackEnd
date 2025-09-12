import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './knowledge-article.reducer';

export const KnowledgeArticleUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const knowledgeArticleEntity = useAppSelector(state => state.knowledgeArticle.entity);
  const loading = useAppSelector(state => state.knowledgeArticle.loading);
  const updating = useAppSelector(state => state.knowledgeArticle.updating);
  const updateSuccess = useAppSelector(state => state.knowledgeArticle.updateSuccess);

  const handleClose = () => {
    navigate(`/knowledge-article${location.search}`);
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
    if (values.categoryId !== undefined && typeof values.categoryId !== 'number') {
      values.categoryId = Number(values.categoryId);
    }
    if (values.views !== undefined && typeof values.views !== 'number') {
      values.views = Number(values.views);
    }
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...knowledgeArticleEntity,
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
          updatedAt: displayDefaultDateTime(),
        }
      : {
          ...knowledgeArticleEntity,
          updatedAt: convertDateTimeFromServer(knowledgeArticleEntity.updatedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.knowledgeArticle.home.createOrEditLabel" data-cy="KnowledgeArticleCreateUpdateHeading">
            <Translate contentKey="lumiApp.knowledgeArticle.home.createOrEditLabel">Create or edit a KnowledgeArticle</Translate>
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
                  id="knowledge-article-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.knowledgeArticle.categoryId')}
                id="knowledge-article-categoryId"
                name="categoryId"
                data-cy="categoryId"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.knowledgeArticle.title')}
                id="knowledge-article-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 5, message: translate('entity.validation.minlength', { min: 5 }) },
                  maxLength: { value: 200, message: translate('entity.validation.maxlength', { max: 200 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.knowledgeArticle.content')}
                id="knowledge-article-content"
                name="content"
                data-cy="content"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.knowledgeArticle.published')}
                id="knowledge-article-published"
                name="published"
                data-cy="published"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('lumiApp.knowledgeArticle.views')}
                id="knowledge-article-views"
                name="views"
                data-cy="views"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.knowledgeArticle.updatedAt')}
                id="knowledge-article-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/knowledge-article" replace color="info">
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

export default KnowledgeArticleUpdate;
