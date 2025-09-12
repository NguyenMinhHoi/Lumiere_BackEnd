import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Supplement from './supplement';
import SupplementDetail from './supplement-detail';
import SupplementUpdate from './supplement-update';
import SupplementDeleteDialog from './supplement-delete-dialog';

const SupplementRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Supplement />} />
    <Route path="new" element={<SupplementUpdate />} />
    <Route path=":id">
      <Route index element={<SupplementDetail />} />
      <Route path="edit" element={<SupplementUpdate />} />
      <Route path="delete" element={<SupplementDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SupplementRoutes;
