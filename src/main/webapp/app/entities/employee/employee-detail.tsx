import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './employee.reducer';

export const EmployeeDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const employeeEntity = useAppSelector(state => state.employee.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="employeeDetailsHeading">
          <Translate contentKey="lumiApp.employee.detail.title">Employee</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.id}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="lumiApp.employee.code">Code</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.code}</dd>
          <dt>
            <span id="fullName">
              <Translate contentKey="lumiApp.employee.fullName">Full Name</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.fullName}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="lumiApp.employee.email">Email</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.email}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="lumiApp.employee.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.phone}</dd>
          <dt>
            <span id="role">
              <Translate contentKey="lumiApp.employee.role">Role</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.role}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.employee.status">Status</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.status}</dd>
          <dt>
            <span id="department">
              <Translate contentKey="lumiApp.employee.department">Department</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.department}</dd>
          <dt>
            <span id="joinedAt">
              <Translate contentKey="lumiApp.employee.joinedAt">Joined At</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.joinedAt ? <TextFormat value={employeeEntity.joinedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.employee.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.createdAt ? <TextFormat value={employeeEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.employee.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.updatedAt ? <TextFormat value={employeeEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/employee" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/employee/${employeeEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default EmployeeDetail;
