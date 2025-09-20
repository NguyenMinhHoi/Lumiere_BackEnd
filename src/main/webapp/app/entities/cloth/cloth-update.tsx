import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ClothStatus } from 'app/shared/model/enumerations/cloth-status.model';
import { createEntity, getEntity, reset, updateEntity } from './cloth.reducer';

export const ClothUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const clothEntity = useAppSelector(state => state.cloth.entity);
  const loading = useAppSelector(state => state.cloth.loading);
  const updating = useAppSelector(state => state.cloth.updating);
  const updateSuccess = useAppSelector(state => state.cloth.updateSuccess);
  const clothStatusValues = Object.keys(ClothStatus);

  const handleClose = () => {
    navigate(`/cloth${location.search}`);
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
    if (values.width !== undefined && typeof values.width !== 'number') {
      values.width = Number(values.width);
    }
    if (values.length !== undefined && typeof values.length !== 'number') {
      values.length = Number(values.length);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...clothEntity,
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
          status: 'AVAILABLE',
          ...clothEntity,
          createdAt: convertDateTimeFromServer(clothEntity.createdAt),
          updatedAt: convertDateTimeFromServer(clothEntity.updatedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.cloth.home.createOrEditLabel" data-cy="ClothCreateUpdateHeading">
            <Translate contentKey="lumiApp.cloth.home.createOrEditLabel">Create or edit a Cloth</Translate>
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
                  id="cloth-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.cloth.code')}
                id="cloth-code"
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
                label={translate('lumiApp.cloth.name')}
                id="cloth-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 2, message: translate('entity.validation.minlength', { min: 2 }) },
                  maxLength: { value: 128, message: translate('entity.validation.maxlength', { max: 128 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.cloth.material')}
                id="cloth-material"
                name="material"
                data-cy="material"
                type="text"
                validate={{
                  maxLength: { value: 128, message: translate('entity.validation.maxlength', { max: 128 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.cloth.color')}
                id="cloth-color"
                name="color"
                data-cy="color"
                type="text"
                validate={{
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <ValidatedField label={translate('lumiApp.cloth.width')} id="cloth-width" name="width" data-cy="width" type="text" />
              <ValidatedField label={translate('lumiApp.cloth.length')} id="cloth-length" name="length" data-cy="length" type="text" />
              <ValidatedField
                label={translate('lumiApp.cloth.unit')}
                id="cloth-unit"
                name="unit"
                data-cy="unit"
                type="text"
                validate={{
                  maxLength: { value: 16, message: translate('entity.validation.maxlength', { max: 16 }) },
                }}
              />
              <ValidatedField label={translate('lumiApp.cloth.status')} id="cloth-status" name="status" data-cy="status" type="select">
                {clothStatusValues.map(clothStatus => (
                  <option value={clothStatus} key={clothStatus}>
                    {translate(`lumiApp.ClothStatus.${clothStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.cloth.createdAt')}
                id="cloth-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.cloth.updatedAt')}
                id="cloth-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/cloth" replace color="info">
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

export default ClothUpdate;
