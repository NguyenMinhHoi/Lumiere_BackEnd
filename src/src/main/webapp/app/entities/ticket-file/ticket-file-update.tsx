import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { StorageType } from 'app/shared/model/enumerations/storage-type.model';
import { FileStatus } from 'app/shared/model/enumerations/file-status.model';
import { createEntity, getEntity, reset, updateEntity } from './ticket-file.reducer';

export const TicketFileUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const ticketFileEntity = useAppSelector(state => state.ticketFile.entity);
  const loading = useAppSelector(state => state.ticketFile.loading);
  const updating = useAppSelector(state => state.ticketFile.updating);
  const updateSuccess = useAppSelector(state => state.ticketFile.updateSuccess);
  const storageTypeValues = Object.keys(StorageType);
  const fileStatusValues = Object.keys(FileStatus);

  const handleClose = () => {
    navigate(`/ticket-file${location.search}`);
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
    if (values.ticketId !== undefined && typeof values.ticketId !== 'number') {
      values.ticketId = Number(values.ticketId);
    }
    if (values.uploaderId !== undefined && typeof values.uploaderId !== 'number') {
      values.uploaderId = Number(values.uploaderId);
    }
    if (values.capacity !== undefined && typeof values.capacity !== 'number') {
      values.capacity = Number(values.capacity);
    }
    values.uploadedAt = convertDateTimeToServer(values.uploadedAt);

    const entity = {
      ...ticketFileEntity,
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
          uploadedAt: displayDefaultDateTime(),
        }
      : {
          storageType: 'LOCAL',
          status: 'ACTIVE',
          ...ticketFileEntity,
          uploadedAt: convertDateTimeFromServer(ticketFileEntity.uploadedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.ticketFile.home.createOrEditLabel" data-cy="TicketFileCreateUpdateHeading">
            <Translate contentKey="lumiApp.ticketFile.home.createOrEditLabel">Create or edit a TicketFile</Translate>
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
                  id="ticket-file-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('lumiApp.ticketFile.ticketId')}
                id="ticket-file-ticketId"
                name="ticketId"
                data-cy="ticketId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.ticketFile.uploaderId')}
                id="ticket-file-uploaderId"
                name="uploaderId"
                data-cy="uploaderId"
                type="text"
              />
              <ValidatedField
                label={translate('lumiApp.ticketFile.fileName')}
                id="ticket-file-fileName"
                name="fileName"
                data-cy="fileName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.ticketFile.originalName')}
                id="ticket-file-originalName"
                name="originalName"
                data-cy="originalName"
                type="text"
                validate={{
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.ticketFile.contentType')}
                id="ticket-file-contentType"
                name="contentType"
                data-cy="contentType"
                type="text"
                validate={{
                  maxLength: { value: 128, message: translate('entity.validation.maxlength', { max: 128 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.ticketFile.capacity')}
                id="ticket-file-capacity"
                name="capacity"
                data-cy="capacity"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('lumiApp.ticketFile.storageType')}
                id="ticket-file-storageType"
                name="storageType"
                data-cy="storageType"
                type="select"
              >
                {storageTypeValues.map(storageType => (
                  <option value={storageType} key={storageType}>
                    {translate(`lumiApp.StorageType.${storageType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.ticketFile.path')}
                id="ticket-file-path"
                name="path"
                data-cy="path"
                type="text"
                validate={{
                  maxLength: { value: 512, message: translate('entity.validation.maxlength', { max: 512 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.ticketFile.url')}
                id="ticket-file-url"
                name="url"
                data-cy="url"
                type="text"
                validate={{
                  maxLength: { value: 1024, message: translate('entity.validation.maxlength', { max: 1024 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.ticketFile.checksum')}
                id="ticket-file-checksum"
                name="checksum"
                data-cy="checksum"
                type="text"
                validate={{
                  maxLength: { value: 128, message: translate('entity.validation.maxlength', { max: 128 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.ticketFile.status')}
                id="ticket-file-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {fileStatusValues.map(fileStatus => (
                  <option value={fileStatus} key={fileStatus}>
                    {translate(`lumiApp.FileStatus.${fileStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('lumiApp.ticketFile.uploadedAt')}
                id="ticket-file-uploadedAt"
                name="uploadedAt"
                data-cy="uploadedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/ticket-file" replace color="info">
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

export default TicketFileUpdate;
