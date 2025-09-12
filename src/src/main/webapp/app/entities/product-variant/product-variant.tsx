import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Col, Form, FormGroup, Input, InputGroup, Row, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities, searchEntities } from './product-variant.reducer';

export const ProductVariant = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const productVariantList = useAppSelector(state => state.productVariant.entities);
  const loading = useAppSelector(state => state.productVariant.loading);
  const totalItems = useAppSelector(state => state.productVariant.totalItems);

  const getAllEntities = () => {
    if (search) {
      dispatch(
        searchEntities({
          query: search,
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        }),
      );
    } else {
      dispatch(
        getEntities({
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        }),
      );
    }
  };

  const startSearching = e => {
    if (search) {
      setPaginationState({
        ...paginationState,
        activePage: 1,
      });
      dispatch(
        searchEntities({
          query: search,
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        }),
      );
    }
    e.preventDefault();
  };

  const clear = () => {
    setSearch('');
    setPaginationState({
      ...paginationState,
      activePage: 1,
    });
    dispatch(getEntities({}));
  };

  const handleSearch = event => setSearch(event.target.value);

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort, search]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="product-variant-heading" data-cy="ProductVariantHeading">
        <Translate contentKey="lumiApp.productVariant.home.title">Product Variants</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.productVariant.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/product-variant/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.productVariant.home.createLabel">Create new Product Variant</Translate>
          </Link>
        </div>
      </h2>
      <Row>
        <Col sm="12">
          <Form onSubmit={startSearching}>
            <FormGroup>
              <InputGroup>
                <Input
                  type="text"
                  name="search"
                  defaultValue={search}
                  onChange={handleSearch}
                  placeholder={translate('lumiApp.productVariant.home.search')}
                />
                <Button className="input-group-addon">
                  <FontAwesomeIcon icon="search" />
                </Button>
                <Button type="reset" className="input-group-addon" onClick={clear}>
                  <FontAwesomeIcon icon="trash" />
                </Button>
              </InputGroup>
            </FormGroup>
          </Form>
        </Col>
      </Row>
      <div className="table-responsive">
        {productVariantList && productVariantList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.productVariant.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('productId')}>
                  <Translate contentKey="lumiApp.productVariant.productId">Product Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('productId')} />
                </th>
                <th className="hand" onClick={sort('sku')}>
                  <Translate contentKey="lumiApp.productVariant.sku">Sku</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sku')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  <Translate contentKey="lumiApp.productVariant.name">Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('price')}>
                  <Translate contentKey="lumiApp.productVariant.price">Price</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('price')} />
                </th>
                <th className="hand" onClick={sort('compareAtPrice')}>
                  <Translate contentKey="lumiApp.productVariant.compareAtPrice">Compare At Price</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('compareAtPrice')} />
                </th>
                <th className="hand" onClick={sort('currency')}>
                  <Translate contentKey="lumiApp.productVariant.currency">Currency</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('currency')} />
                </th>
                <th className="hand" onClick={sort('stockQuantity')}>
                  <Translate contentKey="lumiApp.productVariant.stockQuantity">Stock Quantity</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('stockQuantity')} />
                </th>
                <th className="hand" onClick={sort('weight')}>
                  <Translate contentKey="lumiApp.productVariant.weight">Weight</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('weight')} />
                </th>
                <th className="hand" onClick={sort('length')}>
                  <Translate contentKey="lumiApp.productVariant.length">Length</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('length')} />
                </th>
                <th className="hand" onClick={sort('width')}>
                  <Translate contentKey="lumiApp.productVariant.width">Width</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('width')} />
                </th>
                <th className="hand" onClick={sort('height')}>
                  <Translate contentKey="lumiApp.productVariant.height">Height</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('height')} />
                </th>
                <th className="hand" onClick={sort('isDefault')}>
                  <Translate contentKey="lumiApp.productVariant.isDefault">Is Default</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isDefault')} />
                </th>
                <th className="hand" onClick={sort('createdAt')}>
                  <Translate contentKey="lumiApp.productVariant.createdAt">Created At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                </th>
                <th className="hand" onClick={sort('updatedAt')}>
                  <Translate contentKey="lumiApp.productVariant.updatedAt">Updated At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('updatedAt')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {productVariantList.map((productVariant, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/product-variant/${productVariant.id}`} color="link" size="sm">
                      {productVariant.id}
                    </Button>
                  </td>
                  <td>{productVariant.productId}</td>
                  <td>{productVariant.sku}</td>
                  <td>{productVariant.name}</td>
                  <td>{productVariant.price}</td>
                  <td>{productVariant.compareAtPrice}</td>
                  <td>{productVariant.currency}</td>
                  <td>{productVariant.stockQuantity}</td>
                  <td>{productVariant.weight}</td>
                  <td>{productVariant.length}</td>
                  <td>{productVariant.width}</td>
                  <td>{productVariant.height}</td>
                  <td>{productVariant.isDefault ? 'true' : 'false'}</td>
                  <td>
                    {productVariant.createdAt ? <TextFormat type="date" value={productVariant.createdAt} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>
                    {productVariant.updatedAt ? <TextFormat type="date" value={productVariant.updatedAt} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/product-variant/${productVariant.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/product-variant/${productVariant.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/product-variant/${productVariant.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="lumiApp.productVariant.home.notFound">No Product Variants found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={productVariantList && productVariantList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default ProductVariant;
