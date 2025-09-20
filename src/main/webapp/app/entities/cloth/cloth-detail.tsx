import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './cloth.reducer';

export const ClothDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const clothEntity = useAppSelector(state => state.cloth.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="clothDetailsHeading">
          <Translate contentKey="lumiApp.cloth.detail.title">Cloth</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{clothEntity.id}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="lumiApp.cloth.code">Code</Translate>
            </span>
          </dt>
          <dd>{clothEntity.code}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="lumiApp.cloth.name">Name</Translate>
            </span>
          </dt>
          <dd>{clothEntity.name}</dd>
          <dt>
            <span id="material">
              <Translate contentKey="lumiApp.cloth.material">Material</Translate>
            </span>
          </dt>
          <dd>{clothEntity.material}</dd>
          <dt>
            <span id="color">
              <Translate contentKey="lumiApp.cloth.color">Color</Translate>
            </span>
          </dt>
          <dd>{clothEntity.color}</dd>
          <dt>
            <span id="width">
              <Translate contentKey="lumiApp.cloth.width">Width</Translate>
            </span>
          </dt>
          <dd>{clothEntity.width}</dd>
          <dt>
            <span id="length">
              <Translate contentKey="lumiApp.cloth.length">Length</Translate>
            </span>
          </dt>
          <dd>{clothEntity.length}</dd>
          <dt>
            <span id="unit">
              <Translate contentKey="lumiApp.cloth.unit">Unit</Translate>
            </span>
          </dt>
          <dd>{clothEntity.unit}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.cloth.status">Status</Translate>
            </span>
          </dt>
          <dd>{clothEntity.status}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.cloth.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{clothEntity.createdAt ? <TextFormat value={clothEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.cloth.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>{clothEntity.updatedAt ? <TextFormat value={clothEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/cloth" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/cloth/${clothEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ClothDetail;
