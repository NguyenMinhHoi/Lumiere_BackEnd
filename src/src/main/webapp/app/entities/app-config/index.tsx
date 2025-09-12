import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AppConfig from './app-config';
import AppConfigDetail from './app-config-detail';
import AppConfigUpdate from './app-config-update';
import AppConfigDeleteDialog from './app-config-delete-dialog';

const AppConfigRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<AppConfig />} />
    <Route path="new" element={<AppConfigUpdate />} />
    <Route path=":id">
      <Route index element={<AppConfigDetail />} />
      <Route path="edit" element={<AppConfigUpdate />} />
      <Route path="delete" element={<AppConfigDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AppConfigRoutes;
