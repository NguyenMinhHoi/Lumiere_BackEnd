import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { EmployeeRole } from 'app/shared/model/enumerations/employee-role.model';
import { EmployeeStatus } from 'app/shared/model/enumerations/employee-status.model';
import { createEntity, getEntity, reset, updateEntity } from './employee.reducer';

export const EmployeeUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const employeeEntity = useAppSelector(state => state.employee.entity);
  const loading = useAppSelector(state => state.employee.loading);
  const updating = useAppSelector(state => state.employee.updating);
  const updateSuccess = useAppSelector(state => state.employee.updateSuccess);
  const employeeRoleValues = Object.keys(EmployeeRole);
  const employeeStatusValues = Object.keys(EmployeeStatus);

  const handleClose = () => {
    navigate(`/employee${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.joinedAt = convertDateTimeToServer(values.joinedAt);
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...employeeEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          joinedAt: displayDefaultDateTime(),
          createdAt: displayDefaultDateTime(),
          updatedAt: displayDefaultDateTime(),
        }
      : {
          role: 'AGENT',
          status: 'ACTIVE',
          ...employeeEntity,
          joinedAt: convertDateTimeFromServer(employeeEntity.joinedAt),
          createdAt: convertDateTimeFromServer(employeeEntity.createdAt),
          updatedAt: convertDateTimeFromServer(employeeEntity.updatedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.employee.home.createOrEditLabel" data-cy="EmployeeCreateUpdateHeading">
            <Translate contentKey="lumiApp.employee.home.createOrEditLabel">Create or edit a Employee</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="employee-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.employee.code')}
                id="employee-code"
                name="code"
                data-cy="code"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 3, message: translate('entity.validation.minlength', { min: 3 }) },
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.employee.fullName')}
                id="employee-fullName"
                name="fullName"
                data-cy="fullName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 2, message: translate('entity.validation.minlength', { min: 2 }) },
                  maxLength: { value: 128, message: translate('entity.validation.maxlength', { max: 128 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.employee.email')}
                id="employee-email"
                name="email"
                data-cy="email"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  pattern: {
                    value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                    message: translate('entity.validation.pattern', { pattern: '^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$' }),
                  },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.employee.phone')}
                id="employee-phone"
                name="phone"
                data-cy="phone"
                type="text"
                validate={{
                  pattern: {
                    value: /^[0-9+()\-\s]{6,20}$/,
                    message: translate('entity.validation.pattern', { pattern: '^[0-9+()\\-\\s]{6,20}$' }),
                  },
                }}
              />
              <ValidatedField label={translate('lumiApp.employee.role')} id="employee-role" name="role" data-cy="role" type="select">
                {employeeRoleValues.map(employeeRole => (
                  <option value={employeeRole} key={employeeRole}>
                    {translate(`lumiApp.EmployeeRole.${employeeRole}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.employee.status')}
                id="employee-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {employeeStatusValues.map(employeeStatus => (
                  <option value={employeeStatus} key={employeeStatus}>
                    {translate(`lumiApp.EmployeeStatus.${employeeStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.employee.department')}
                id="employee-department"
                name="department"
                data-cy="department"
                type="text"
                validate={{
                  maxLength: { value: 128, message: translate('entity.validation.maxlength', { max: 128 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.employee.joinedAt')}
                id="employee-joinedAt"
                name="joinedAt"
                data-cy="joinedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('lumiApp.employee.createdAt')}
                id="employee-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.employee.updatedAt')}
                id="employee-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/employee" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default EmployeeUpdate;
