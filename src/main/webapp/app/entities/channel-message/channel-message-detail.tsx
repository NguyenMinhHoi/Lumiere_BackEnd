import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './channel-message.reducer';

export const ChannelMessageDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const channelMessageEntity = useAppSelector(state => state.channelMessage.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="channelMessageDetailsHeading">
          <Translate contentKey="lumiApp.channelMessage.detail.title">ChannelMessage</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{channelMessageEntity.id}</dd>
          <dt>
            <span id="ticketId">
              <Translate contentKey="lumiApp.channelMessage.ticketId">Ticket Id</Translate>
            </span>
          </dt>
          <dd>{channelMessageEntity.ticketId}</dd>
          <dt>
            <span id="authorId">
              <Translate contentKey="lumiApp.channelMessage.authorId">Author Id</Translate>
            </span>
          </dt>
          <dd>{channelMessageEntity.authorId}</dd>
          <dt>
            <span id="direction">
              <Translate contentKey="lumiApp.channelMessage.direction">Direction</Translate>
            </span>
          </dt>
          <dd>{channelMessageEntity.direction}</dd>
          <dt>
            <span id="content">
              <Translate contentKey="lumiApp.channelMessage.content">Content</Translate>
            </span>
          </dt>
          <dd>{channelMessageEntity.content}</dd>
          <dt>
            <span id="sentAt">
              <Translate contentKey="lumiApp.channelMessage.sentAt">Sent At</Translate>
            </span>
          </dt>
          <dd>
            {channelMessageEntity.sentAt ? <TextFormat value={channelMessageEntity.sentAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="externalMessageId">
              <Translate contentKey="lumiApp.channelMessage.externalMessageId">External Message Id</Translate>
            </span>
          </dt>
          <dd>{channelMessageEntity.externalMessageId}</dd>
        </dl>
        <Button tag={Link} to="/channel-message" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/channel-message/${channelMessageEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ChannelMessageDetail;
