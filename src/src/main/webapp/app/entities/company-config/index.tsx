import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CompanyConfig from './company-config';
import CompanyConfigDetail from './company-config-detail';
import CompanyConfigUpdate from './company-config-update';
import CompanyConfigDeleteDialog from './company-config-delete-dialog';

const CompanyConfigRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CompanyConfig />} />
    <Route path="new" element={<CompanyConfigUpdate />} />
    <Route path=":id">
      <Route index element={<CompanyConfigDetail />} />
      <Route path="edit" element={<CompanyConfigUpdate />} />
      <Route path="delete" element={<CompanyConfigDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CompanyConfigRoutes;
