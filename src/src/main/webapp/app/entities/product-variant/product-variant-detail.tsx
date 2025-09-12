import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './product-variant.reducer';

export const ProductVariantDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const productVariantEntity = useAppSelector(state => state.productVariant.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="productVariantDetailsHeading">
          <Translate contentKey="lumiApp.productVariant.detail.title">ProductVariant</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{productVariantEntity.id}</dd>
          <dt>
            <span id="productId">
              <Translate contentKey="lumiApp.productVariant.productId">Product Id</Translate>
            </span>
          </dt>
          <dd>{productVariantEntity.productId}</dd>
          <dt>
            <span id="sku">
              <Translate contentKey="lumiApp.productVariant.sku">Sku</Translate>
            </span>
          </dt>
          <dd>{productVariantEntity.sku}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="lumiApp.productVariant.name">Name</Translate>
            </span>
          </dt>
          <dd>{productVariantEntity.name}</dd>
          <dt>
            <span id="price">
              <Translate contentKey="lumiApp.productVariant.price">Price</Translate>
            </span>
          </dt>
          <dd>{productVariantEntity.price}</dd>
          <dt>
            <span id="compareAtPrice">
              <Translate contentKey="lumiApp.productVariant.compareAtPrice">Compare At Price</Translate>
            </span>
          </dt>
          <dd>{productVariantEntity.compareAtPrice}</dd>
          <dt>
            <span id="currency">
              <Translate contentKey="lumiApp.productVariant.currency">Currency</Translate>
            </span>
          </dt>
          <dd>{productVariantEntity.currency}</dd>
          <dt>
            <span id="stockQuantity">
              <Translate contentKey="lumiApp.productVariant.stockQuantity">Stock Quantity</Translate>
            </span>
          </dt>
          <dd>{productVariantEntity.stockQuantity}</dd>
          <dt>
            <span id="weight">
              <Translate contentKey="lumiApp.productVariant.weight">Weight</Translate>
            </span>
          </dt>
          <dd>{productVariantEntity.weight}</dd>
          <dt>
            <span id="length">
              <Translate contentKey="lumiApp.productVariant.length">Length</Translate>
            </span>
          </dt>
          <dd>{productVariantEntity.length}</dd>
          <dt>
            <span id="width">
              <Translate contentKey="lumiApp.productVariant.width">Width</Translate>
            </span>
          </dt>
          <dd>{productVariantEntity.width}</dd>
          <dt>
            <span id="height">
              <Translate contentKey="lumiApp.productVariant.height">Height</Translate>
            </span>
          </dt>
          <dd>{productVariantEntity.height}</dd>
          <dt>
            <span id="isDefault">
              <Translate contentKey="lumiApp.productVariant.isDefault">Is Default</Translate>
            </span>
          </dt>
          <dd>{productVariantEntity.isDefault ? 'true' : 'false'}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.productVariant.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {productVariantEntity.createdAt ? (
              <TextFormat value={productVariantEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.productVariant.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>
            {productVariantEntity.updatedAt ? (
              <TextFormat value={productVariantEntity.updatedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
        </dl>
        <Button tag={Link} to="/product-variant" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/product-variant/${productVariantEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProductVariantDetail;
