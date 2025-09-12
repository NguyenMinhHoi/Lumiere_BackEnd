import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import IntegrationLog from './integration-log';
import IntegrationLogDetail from './integration-log-detail';
import IntegrationLogUpdate from './integration-log-update';
import IntegrationLogDeleteDialog from './integration-log-delete-dialog';

const IntegrationLogRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<IntegrationLog />} />
    <Route path="new" element={<IntegrationLogUpdate />} />
    <Route path=":id">
      <Route index element={<IntegrationLogDetail />} />
      <Route path="edit" element={<IntegrationLogUpdate />} />
      <Route path="delete" element={<IntegrationLogDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default IntegrationLogRoutes;
