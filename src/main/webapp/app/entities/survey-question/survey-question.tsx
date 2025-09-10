import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Col, Form, FormGroup, Input, InputGroup, Row, Table } from 'reactstrap';
import { Translate, getSortState, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities, searchEntities } from './survey-question.reducer';

export const SurveyQuestion = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const surveyQuestionList = useAppSelector(state => state.surveyQuestion.entities);
  const loading = useAppSelector(state => state.surveyQuestion.loading);

  const getAllEntities = () => {
    if (search) {
      dispatch(
        searchEntities({
          query: search,
          sort: `${sortState.sort},${sortState.order}`,
        }),
      );
    } else {
      dispatch(
        getEntities({
          sort: `${sortState.sort},${sortState.order}`,
        }),
      );
    }
  };

  const startSearching = e => {
    if (search) {
      dispatch(
        searchEntities({
          query: search,
          sort: `${sortState.sort},${sortState.order}`,
        }),
      );
    }
    e.preventDefault();
  };

  const clear = () => {
    setSearch('');
    dispatch(getEntities({}));
  };

  const handleSearch = event => setSearch(event.target.value);

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort, search]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const order = sortState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="survey-question-heading" data-cy="SurveyQuestionHeading">
        <Translate contentKey="lumiApp.surveyQuestion.home.title">Survey Questions</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.surveyQuestion.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/survey-question/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.surveyQuestion.home.createLabel">Create new Survey Question</Translate>
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
                  placeholder={translate('lumiApp.surveyQuestion.home.search')}
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
        {surveyQuestionList && surveyQuestionList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.surveyQuestion.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('text')}>
                  <Translate contentKey="lumiApp.surveyQuestion.text">Text</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('text')} />
                </th>
                <th className="hand" onClick={sort('questionType')}>
                  <Translate contentKey="lumiApp.surveyQuestion.questionType">Question Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('questionType')} />
                </th>
                <th className="hand" onClick={sort('scaleMin')}>
                  <Translate contentKey="lumiApp.surveyQuestion.scaleMin">Scale Min</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('scaleMin')} />
                </th>
                <th className="hand" onClick={sort('scaleMax')}>
                  <Translate contentKey="lumiApp.surveyQuestion.scaleMax">Scale Max</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('scaleMax')} />
                </th>
                <th className="hand" onClick={sort('isNeed')}>
                  <Translate contentKey="lumiApp.surveyQuestion.isNeed">Is Need</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isNeed')} />
                </th>
                <th className="hand" onClick={sort('orderNo')}>
                  <Translate contentKey="lumiApp.surveyQuestion.orderNo">Order No</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('orderNo')} />
                </th>
                <th>
                  <Translate contentKey="lumiApp.surveyQuestion.survey">Survey</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {surveyQuestionList.map((surveyQuestion, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/survey-question/${surveyQuestion.id}`} color="link" size="sm">
                      {surveyQuestion.id}
                    </Button>
                  </td>
                  <td>{surveyQuestion.text}</td>
                  <td>
                    <Translate contentKey={`lumiApp.QuestionType.${surveyQuestion.questionType}`} />
                  </td>
                  <td>{surveyQuestion.scaleMin}</td>
                  <td>{surveyQuestion.scaleMax}</td>
                  <td>{surveyQuestion.isNeed ? 'true' : 'false'}</td>
                  <td>{surveyQuestion.orderNo}</td>
                  <td>
                    {surveyQuestion.survey ? <Link to={`/survey/${surveyQuestion.survey.id}`}>{surveyQuestion.survey.title}</Link> : ''}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/survey-question/${surveyQuestion.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/survey-question/${surveyQuestion.id}/edit`}
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
                        onClick={() => (window.location.href = `/survey-question/${surveyQuestion.id}/delete`)}
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
              <Translate contentKey="lumiApp.surveyQuestion.home.notFound">No Survey Questions found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default SurveyQuestion;
