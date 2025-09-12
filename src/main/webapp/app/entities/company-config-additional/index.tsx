import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CompanyConfigAdditional from './company-config-additional';
import CompanyConfigAdditionalDetail from './company-config-additional-detail';
import CompanyConfigAdditionalUpdate from './company-config-additional-update';
import CompanyConfigAdditionalDeleteDialog from './company-config-additional-delete-dialog';

const CompanyConfigAdditionalRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CompanyConfigAdditional />} />
    <Route path="new" element={<CompanyConfigAdditionalUpdate />} />
    <Route path=":id">
      <Route index element={<CompanyConfigAdditionalDetail />} />
      <Route path="edit" element={<CompanyConfigAdditionalUpdate />} />
      <Route path="delete" element={<CompanyConfigAdditionalDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CompanyConfigAdditionalRoutes;
