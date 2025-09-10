import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { SupplierStatus } from 'app/shared/model/enumerations/supplier-status.model';
import { createEntity, getEntity, reset, updateEntity } from './supplier.reducer';

export const SupplierUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const supplierEntity = useAppSelector(state => state.supplier.entity);
  const loading = useAppSelector(state => state.supplier.loading);
  const updating = useAppSelector(state => state.supplier.updating);
  const updateSuccess = useAppSelector(state => state.supplier.updateSuccess);
  const supplierStatusValues = Object.keys(SupplierStatus);

  const handleClose = () => {
    navigate(`/supplier${location.search}`);
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
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...supplierEntity,
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
          createdAt: displayDefaultDateTime(),
          updatedAt: displayDefaultDateTime(),
        }
      : {
          status: 'ACTIVE',
          ...supplierEntity,
          createdAt: convertDateTimeFromServer(supplierEntity.createdAt),
          updatedAt: convertDateTimeFromServer(supplierEntity.updatedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.supplier.home.createOrEditLabel" data-cy="SupplierCreateUpdateHeading">
            <Translate contentKey="lumiApp.supplier.home.createOrEditLabel">Create or edit a Supplier</Translate>
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
                  id="supplier-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.supplier.code')}
                id="supplier-code"
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
                label={translate('lumiApp.supplier.name')}
                id="supplier-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 2, message: translate('entity.validation.minlength', { min: 2 }) },
                  maxLength: { value: 200, message: translate('entity.validation.maxlength', { max: 200 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.supplier.email')}
                id="supplier-email"
                name="email"
                data-cy="email"
                type="text"
                validate={{
                  pattern: {
                    value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                    message: translate('entity.validation.pattern', { pattern: '^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$' }),
                  },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.supplier.phone')}
                id="supplier-phone"
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
              <ValidatedField
                label={translate('lumiApp.supplier.address')}
                id="supplier-address"
                name="address"
                data-cy="address"
                type="text"
                validate={{
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.supplier.status')}
                id="supplier-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {supplierStatusValues.map(supplierStatus => (
                  <option value={supplierStatus} key={supplierStatus}>
                    {translate(`lumiApp.SupplierStatus.${supplierStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.supplier.createdAt')}
                id="supplier-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.supplier.updatedAt')}
                id="supplier-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/supplier" replace color="info">
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

export default SupplierUpdate;
