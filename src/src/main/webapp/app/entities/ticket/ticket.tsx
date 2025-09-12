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

import { getEntities, searchEntities } from './ticket.reducer';

export const Ticket = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const ticketList = useAppSelector(state => state.ticket.entities);
  const loading = useAppSelector(state => state.ticket.loading);
  const totalItems = useAppSelector(state => state.ticket.totalItems);

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
      <h2 id="ticket-heading" data-cy="TicketHeading">
        <Translate contentKey="lumiApp.ticket.home.title">Tickets</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.ticket.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/ticket/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.ticket.home.createLabel">Create new Ticket</Translate>
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
                  placeholder={translate('lumiApp.ticket.home.search')}
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
        {ticketList && ticketList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.ticket.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('customerId')}>
                  <Translate contentKey="lumiApp.ticket.customerId">Customer Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('customerId')} />
                </th>
                <th className="hand" onClick={sort('slaPlanId')}>
                  <Translate contentKey="lumiApp.ticket.slaPlanId">Sla Plan Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('slaPlanId')} />
                </th>
                <th className="hand" onClick={sort('orderId')}>
                  <Translate contentKey="lumiApp.ticket.orderId">Order Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('orderId')} />
                </th>
                <th className="hand" onClick={sort('assigneeEmployeeId')}>
                  <Translate contentKey="lumiApp.ticket.assigneeEmployeeId">Assignee Employee Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('assigneeEmployeeId')} />
                </th>
                <th className="hand" onClick={sort('code')}>
                  <Translate contentKey="lumiApp.ticket.code">Code</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('code')} />
                </th>
                <th className="hand" onClick={sort('subject')}>
                  <Translate contentKey="lumiApp.ticket.subject">Subject</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('subject')} />
                </th>
                <th className="hand" onClick={sort('description')}>
                  <Translate contentKey="lumiApp.ticket.description">Description</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="lumiApp.ticket.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('priority')}>
                  <Translate contentKey="lumiApp.ticket.priority">Priority</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('priority')} />
                </th>
                <th className="hand" onClick={sort('channel')}>
                  <Translate contentKey="lumiApp.ticket.channel">Channel</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('channel')} />
                </th>
                <th className="hand" onClick={sort('openedAt')}>
                  <Translate contentKey="lumiApp.ticket.openedAt">Opened At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('openedAt')} />
                </th>
                <th className="hand" onClick={sort('firstResponseAt')}>
                  <Translate contentKey="lumiApp.ticket.firstResponseAt">First Response At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('firstResponseAt')} />
                </th>
                <th className="hand" onClick={sort('resolvedAt')}>
                  <Translate contentKey="lumiApp.ticket.resolvedAt">Resolved At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('resolvedAt')} />
                </th>
                <th className="hand" onClick={sort('slaDueAt')}>
                  <Translate contentKey="lumiApp.ticket.slaDueAt">Sla Due At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('slaDueAt')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {ticketList.map((ticket, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/ticket/${ticket.id}`} color="link" size="sm">
                      {ticket.id}
                    </Button>
                  </td>
                  <td>{ticket.customerId}</td>
                  <td>{ticket.slaPlanId}</td>
                  <td>{ticket.orderId}</td>
                  <td>{ticket.assigneeEmployeeId}</td>
                  <td>{ticket.code}</td>
                  <td>{ticket.subject}</td>
                  <td>{ticket.description}</td>
                  <td>
                    <Translate contentKey={`lumiApp.TicketStatus.${ticket.status}`} />
                  </td>
                  <td>
                    <Translate contentKey={`lumiApp.Priority.${ticket.priority}`} />
                  </td>
                  <td>
                    <Translate contentKey={`lumiApp.ChannelType.${ticket.channel}`} />
                  </td>
                  <td>{ticket.openedAt ? <TextFormat type="date" value={ticket.openedAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>
                    {ticket.firstResponseAt ? <TextFormat type="date" value={ticket.firstResponseAt} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>{ticket.resolvedAt ? <TextFormat type="date" value={ticket.resolvedAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{ticket.slaDueAt ? <TextFormat type="date" value={ticket.slaDueAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/ticket/${ticket.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/ticket/${ticket.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/ticket/${ticket.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="lumiApp.ticket.home.notFound">No Tickets found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={ticketList && ticketList.length > 0 ? '' : 'd-none'}>
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

export default Ticket;
