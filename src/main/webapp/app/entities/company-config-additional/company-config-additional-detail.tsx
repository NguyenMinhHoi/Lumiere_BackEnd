import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './company-config-additional.reducer';

export const CompanyConfigAdditionalDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const companyConfigAdditionalEntity = useAppSelector(state => state.companyConfigAdditional.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="companyConfigAdditionalDetailsHeading">
          <Translate contentKey="lumiApp.companyConfigAdditional.detail.title">CompanyConfigAdditional</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{companyConfigAdditionalEntity.id}</dd>
          <dt>
            <span id="companyConfigId">
              <Translate contentKey="lumiApp.companyConfigAdditional.companyConfigId">Company Config Id</Translate>
            </span>
          </dt>
          <dd>{companyConfigAdditionalEntity.companyConfigId}</dd>
          <dt>
            <span id="configKey">
              <Translate contentKey="lumiApp.companyConfigAdditional.configKey">Config Key</Translate>
            </span>
          </dt>
          <dd>{companyConfigAdditionalEntity.configKey}</dd>
          <dt>
            <span id="configValue">
              <Translate contentKey="lumiApp.companyConfigAdditional.configValue">Config Value</Translate>
            </span>
          </dt>
          <dd>{companyConfigAdditionalEntity.configValue}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.companyConfigAdditional.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {companyConfigAdditionalEntity.createdAt ? (
              <TextFormat value={companyConfigAdditionalEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.companyConfigAdditional.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>
            {companyConfigAdditionalEntity.updatedAt ? (
              <TextFormat value={companyConfigAdditionalEntity.updatedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
        </dl>
        <Button tag={Link} to="/company-config-additional" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/company-config-additional/${companyConfigAdditionalEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CompanyConfigAdditionalDetail;
