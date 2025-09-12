import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './company-config.reducer';

export const CompanyConfigDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const companyConfigEntity = useAppSelector(state => state.companyConfig.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="companyConfigDetailsHeading">
          <Translate contentKey="lumiApp.companyConfig.detail.title">CompanyConfig</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{companyConfigEntity.id}</dd>
          <dt>
            <span id="companyId">
              <Translate contentKey="lumiApp.companyConfig.companyId">Company Id</Translate>
            </span>
          </dt>
          <dd>{companyConfigEntity.companyId}</dd>
          <dt>
            <span id="appId">
              <Translate contentKey="lumiApp.companyConfig.appId">App Id</Translate>
            </span>
          </dt>
          <dd>{companyConfigEntity.appId}</dd>
          <dt>
            <span id="isActive">
              <Translate contentKey="lumiApp.companyConfig.isActive">Is Active</Translate>
            </span>
          </dt>
          <dd>{companyConfigEntity.isActive ? 'true' : 'false'}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.companyConfig.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {companyConfigEntity.createdAt ? (
              <TextFormat value={companyConfigEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.companyConfig.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>
            {companyConfigEntity.updatedAt ? (
              <TextFormat value={companyConfigEntity.updatedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
        </dl>
        <Button tag={Link} to="/company-config" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/company-config/${companyConfigEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CompanyConfigDetail;
