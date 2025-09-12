import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './campaign.reducer';

export const CampaignDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const campaignEntity = useAppSelector(state => state.campaign.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="campaignDetailsHeading">
          <Translate contentKey="lumiApp.campaign.detail.title">Campaign</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{campaignEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="lumiApp.campaign.name">Name</Translate>
            </span>
          </dt>
          <dd>{campaignEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="lumiApp.campaign.description">Description</Translate>
            </span>
          </dt>
          <dd>{campaignEntity.description}</dd>
          <dt>
            <span id="channel">
              <Translate contentKey="lumiApp.campaign.channel">Channel</Translate>
            </span>
          </dt>
          <dd>{campaignEntity.channel}</dd>
          <dt>
            <span id="budget">
              <Translate contentKey="lumiApp.campaign.budget">Budget</Translate>
            </span>
          </dt>
          <dd>{campaignEntity.budget}</dd>
          <dt>
            <span id="startDate">
              <Translate contentKey="lumiApp.campaign.startDate">Start Date</Translate>
            </span>
          </dt>
          <dd>{campaignEntity.startDate ? <TextFormat value={campaignEntity.startDate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="endDate">
              <Translate contentKey="lumiApp.campaign.endDate">End Date</Translate>
            </span>
          </dt>
          <dd>{campaignEntity.endDate ? <TextFormat value={campaignEntity.endDate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="isActive">
              <Translate contentKey="lumiApp.campaign.isActive">Is Active</Translate>
            </span>
          </dt>
          <dd>{campaignEntity.isActive ? 'true' : 'false'}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.campaign.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{campaignEntity.createdAt ? <TextFormat value={campaignEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.campaign.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>{campaignEntity.updatedAt ? <TextFormat value={campaignEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/campaign" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/campaign/${campaignEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CampaignDetail;
