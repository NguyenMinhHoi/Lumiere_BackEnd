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

import { getEntities, searchEntities } from './cloth-audit.reducer';

export const ClothAudit = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const clothAuditList = useAppSelector(state => state.clothAudit.entities);
  const loading = useAppSelector(state => state.clothAudit.loading);
  const totalItems = useAppSelector(state => state.clothAudit.totalItems);

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
      <h2 id="cloth-audit-heading" data-cy="ClothAuditHeading">
        <Translate contentKey="lumiApp.clothAudit.home.title">Cloth Audits</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.clothAudit.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/cloth-audit/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.clothAudit.home.createLabel">Create new Cloth Audit</Translate>
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
                  placeholder={translate('lumiApp.clothAudit.home.search')}
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
        {clothAuditList && clothAuditList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.clothAudit.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('clothId')}>
                  <Translate contentKey="lumiApp.clothAudit.clothId">Cloth Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('clothId')} />
                </th>
                <th className="hand" onClick={sort('supplierId')}>
                  <Translate contentKey="lumiApp.clothAudit.supplierId">Supplier Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('supplierId')} />
                </th>
                <th className="hand" onClick={sort('productId')}>
                  <Translate contentKey="lumiApp.clothAudit.productId">Product Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('productId')} />
                </th>
                <th className="hand" onClick={sort('action')}>
                  <Translate contentKey="lumiApp.clothAudit.action">Action</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('action')} />
                </th>
                <th className="hand" onClick={sort('quantity')}>
                  <Translate contentKey="lumiApp.clothAudit.quantity">Quantity</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('quantity')} />
                </th>
                <th className="hand" onClick={sort('unit')}>
                  <Translate contentKey="lumiApp.clothAudit.unit">Unit</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('unit')} />
                </th>
                <th className="hand" onClick={sort('sentAt')}>
                  <Translate contentKey="lumiApp.clothAudit.sentAt">Sent At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sentAt')} />
                </th>
                <th className="hand" onClick={sort('note')}>
                  <Translate contentKey="lumiApp.clothAudit.note">Note</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('note')} />
                </th>
                <th className="hand" onClick={sort('createdAt')}>
                  <Translate contentKey="lumiApp.clothAudit.createdAt">Created At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {clothAuditList.map((clothAudit, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/cloth-audit/${clothAudit.id}`} color="link" size="sm">
                      {clothAudit.id}
                    </Button>
                  </td>
                  <td>{clothAudit.clothId}</td>
                  <td>{clothAudit.supplierId}</td>
                  <td>{clothAudit.productId}</td>
                  <td>
                    <Translate contentKey={`lumiApp.AuditAction.${clothAudit.action}`} />
                  </td>
                  <td>{clothAudit.quantity}</td>
                  <td>{clothAudit.unit}</td>
                  <td>{clothAudit.sentAt ? <TextFormat type="date" value={clothAudit.sentAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{clothAudit.note}</td>
                  <td>{clothAudit.createdAt ? <TextFormat type="date" value={clothAudit.createdAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/cloth-audit/${clothAudit.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/cloth-audit/${clothAudit.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/cloth-audit/${clothAudit.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="lumiApp.clothAudit.home.notFound">No Cloth Audits found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={clothAuditList && clothAuditList.length > 0 ? '' : 'd-none'}>
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

export default ClothAudit;
