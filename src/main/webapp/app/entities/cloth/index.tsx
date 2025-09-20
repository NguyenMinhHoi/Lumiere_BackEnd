import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Cloth from './cloth';
import ClothDetail from './cloth-detail';
import ClothUpdate from './cloth-update';
import ClothDeleteDialog from './cloth-delete-dialog';

const ClothRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Cloth />} />
    <Route path="new" element={<ClothUpdate />} />
    <Route path=":id">
      <Route index element={<ClothDetail />} />
      <Route path="edit" element={<ClothUpdate />} />
      <Route path="delete" element={<ClothDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ClothRoutes;
