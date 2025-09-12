import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './cart-item.reducer';

export const CartItemDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const cartItemEntity = useAppSelector(state => state.cartItem.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="cartItemDetailsHeading">
          <Translate contentKey="lumiApp.cartItem.detail.title">CartItem</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{cartItemEntity.id}</dd>
          <dt>
            <span id="cartId">
              <Translate contentKey="lumiApp.cartItem.cartId">Cart Id</Translate>
            </span>
          </dt>
          <dd>{cartItemEntity.cartId}</dd>
          <dt>
            <span id="productVariantId">
              <Translate contentKey="lumiApp.cartItem.productVariantId">Product Variant Id</Translate>
            </span>
          </dt>
          <dd>{cartItemEntity.productVariantId}</dd>
          <dt>
            <span id="quantity">
              <Translate contentKey="lumiApp.cartItem.quantity">Quantity</Translate>
            </span>
          </dt>
          <dd>{cartItemEntity.quantity}</dd>
          <dt>
            <span id="addedAt">
              <Translate contentKey="lumiApp.cartItem.addedAt">Added At</Translate>
            </span>
          </dt>
          <dd>{cartItemEntity.addedAt ? <TextFormat value={cartItemEntity.addedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/cart-item" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/cart-item/${cartItemEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CartItemDetail;
