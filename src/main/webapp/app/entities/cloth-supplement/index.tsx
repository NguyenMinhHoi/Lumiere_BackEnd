import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ClothSupplement from './cloth-supplement';
import ClothSupplementDetail from './cloth-supplement-detail';
import ClothSupplementUpdate from './cloth-supplement-update';
import ClothSupplementDeleteDialog from './cloth-supplement-delete-dialog';

const ClothSupplementRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ClothSupplement />} />
    <Route path="new" element={<ClothSupplementUpdate />} />
    <Route path=":id">
      <Route index element={<ClothSupplementDetail />} />
      <Route path="edit" element={<ClothSupplementUpdate />} />
      <Route path="delete" element={<ClothSupplementDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ClothSupplementRoutes;
