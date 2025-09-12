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

import { getEntities, searchEntities } from './voucher.reducer';

export const Voucher = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const voucherList = useAppSelector(state => state.voucher.entities);
  const loading = useAppSelector(state => state.voucher.loading);
  const totalItems = useAppSelector(state => state.voucher.totalItems);

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
      <h2 id="voucher-heading" data-cy="VoucherHeading">
        <Translate contentKey="lumiApp.voucher.home.title">Vouchers</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.voucher.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/voucher/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.voucher.home.createLabel">Create new Voucher</Translate>
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
                  placeholder={translate('lumiApp.voucher.home.search')}
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
        {voucherList && voucherList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.voucher.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('code')}>
                  <Translate contentKey="lumiApp.voucher.code">Code</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('code')} />
                </th>
                <th className="hand" onClick={sort('discountType')}>
                  <Translate contentKey="lumiApp.voucher.discountType">Discount Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('discountType')} />
                </th>
                <th className="hand" onClick={sort('discountValue')}>
                  <Translate contentKey="lumiApp.voucher.discountValue">Discount Value</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('discountValue')} />
                </th>
                <th className="hand" onClick={sort('minOrderValue')}>
                  <Translate contentKey="lumiApp.voucher.minOrderValue">Min Order Value</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('minOrderValue')} />
                </th>
                <th className="hand" onClick={sort('maxDiscountValue')}>
                  <Translate contentKey="lumiApp.voucher.maxDiscountValue">Max Discount Value</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('maxDiscountValue')} />
                </th>
                <th className="hand" onClick={sort('usageLimit')}>
                  <Translate contentKey="lumiApp.voucher.usageLimit">Usage Limit</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('usageLimit')} />
                </th>
                <th className="hand" onClick={sort('usedCount')}>
                  <Translate contentKey="lumiApp.voucher.usedCount">Used Count</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('usedCount')} />
                </th>
                <th className="hand" onClick={sort('validFrom')}>
                  <Translate contentKey="lumiApp.voucher.validFrom">Valid From</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('validFrom')} />
                </th>
                <th className="hand" onClick={sort('validTo')}>
                  <Translate contentKey="lumiApp.voucher.validTo">Valid To</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('validTo')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="lumiApp.voucher.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('createdAt')}>
                  <Translate contentKey="lumiApp.voucher.createdAt">Created At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                </th>
                <th className="hand" onClick={sort('updatedAt')}>
                  <Translate contentKey="lumiApp.voucher.updatedAt">Updated At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('updatedAt')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {voucherList.map((voucher, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/voucher/${voucher.id}`} color="link" size="sm">
                      {voucher.id}
                    </Button>
                  </td>
                  <td>{voucher.code}</td>
                  <td>
                    <Translate contentKey={`lumiApp.VoucherType.${voucher.discountType}`} />
                  </td>
                  <td>{voucher.discountValue}</td>
                  <td>{voucher.minOrderValue}</td>
                  <td>{voucher.maxDiscountValue}</td>
                  <td>{voucher.usageLimit}</td>
                  <td>{voucher.usedCount}</td>
                  <td>{voucher.validFrom ? <TextFormat type="date" value={voucher.validFrom} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{voucher.validTo ? <TextFormat type="date" value={voucher.validTo} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>
                    <Translate contentKey={`lumiApp.VoucherStatus.${voucher.status}`} />
                  </td>
                  <td>{voucher.createdAt ? <TextFormat type="date" value={voucher.createdAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{voucher.updatedAt ? <TextFormat type="date" value={voucher.updatedAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/voucher/${voucher.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/voucher/${voucher.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/voucher/${voucher.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="lumiApp.voucher.home.notFound">No Vouchers found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={voucherList && voucherList.length > 0 ? '' : 'd-none'}>
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

export default Voucher;
