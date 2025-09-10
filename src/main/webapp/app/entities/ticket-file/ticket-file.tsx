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

import { getEntities, searchEntities } from './ticket-file.reducer';

export const TicketFile = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const ticketFileList = useAppSelector(state => state.ticketFile.entities);
  const loading = useAppSelector(state => state.ticketFile.loading);
  const totalItems = useAppSelector(state => state.ticketFile.totalItems);

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
      <h2 id="ticket-file-heading" data-cy="TicketFileHeading">
        <Translate contentKey="lumiApp.ticketFile.home.title">Ticket Files</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.ticketFile.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/ticket-file/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.ticketFile.home.createLabel">Create new Ticket File</Translate>
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
                  placeholder={translate('lumiApp.ticketFile.home.search')}
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
        {ticketFileList && ticketFileList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.ticketFile.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('fileName')}>
                  <Translate contentKey="lumiApp.ticketFile.fileName">File Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('fileName')} />
                </th>
                <th className="hand" onClick={sort('originalName')}>
                  <Translate contentKey="lumiApp.ticketFile.originalName">Original Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('originalName')} />
                </th>
                <th className="hand" onClick={sort('contentType')}>
                  <Translate contentKey="lumiApp.ticketFile.contentType">Content Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('contentType')} />
                </th>
                <th className="hand" onClick={sort('capacity')}>
                  <Translate contentKey="lumiApp.ticketFile.capacity">Capacity</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('capacity')} />
                </th>
                <th className="hand" onClick={sort('storageType')}>
                  <Translate contentKey="lumiApp.ticketFile.storageType">Storage Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('storageType')} />
                </th>
                <th className="hand" onClick={sort('path')}>
                  <Translate contentKey="lumiApp.ticketFile.path">Path</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('path')} />
                </th>
                <th className="hand" onClick={sort('url')}>
                  <Translate contentKey="lumiApp.ticketFile.url">Url</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('url')} />
                </th>
                <th className="hand" onClick={sort('checksum')}>
                  <Translate contentKey="lumiApp.ticketFile.checksum">Checksum</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('checksum')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="lumiApp.ticketFile.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('uploadedAt')}>
                  <Translate contentKey="lumiApp.ticketFile.uploadedAt">Uploaded At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('uploadedAt')} />
                </th>
                <th>
                  <Translate contentKey="lumiApp.ticketFile.ticket">Ticket</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.ticketFile.uploader">Uploader</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {ticketFileList.map((ticketFile, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/ticket-file/${ticketFile.id}`} color="link" size="sm">
                      {ticketFile.id}
                    </Button>
                  </td>
                  <td>{ticketFile.fileName}</td>
                  <td>{ticketFile.originalName}</td>
                  <td>{ticketFile.contentType}</td>
                  <td>{ticketFile.capacity}</td>
                  <td>
                    <Translate contentKey={`lumiApp.StorageType.${ticketFile.storageType}`} />
                  </td>
                  <td>{ticketFile.path}</td>
                  <td>{ticketFile.url}</td>
                  <td>{ticketFile.checksum}</td>
                  <td>
                    <Translate contentKey={`lumiApp.FileStatus.${ticketFile.status}`} />
                  </td>
                  <td>
                    {ticketFile.uploadedAt ? <TextFormat type="date" value={ticketFile.uploadedAt} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>{ticketFile.ticket ? <Link to={`/ticket/${ticketFile.ticket.id}`}>{ticketFile.ticket.code}</Link> : ''}</td>
                  <td>{ticketFile.uploader ? ticketFile.uploader.login : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/ticket-file/${ticketFile.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/ticket-file/${ticketFile.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/ticket-file/${ticketFile.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="lumiApp.ticketFile.home.notFound">No Ticket Files found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={ticketFileList && ticketFileList.length > 0 ? '' : 'd-none'}>
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

export default TicketFile;
