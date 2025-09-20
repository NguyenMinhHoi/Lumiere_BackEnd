import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ClothProductMap from './cloth-product-map';
import ClothProductMapDetail from './cloth-product-map-detail';
import ClothProductMapUpdate from './cloth-product-map-update';
import ClothProductMapDeleteDialog from './cloth-product-map-delete-dialog';

const ClothProductMapRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ClothProductMap />} />
    <Route path="new" element={<ClothProductMapUpdate />} />
    <Route path=":id">
      <Route index element={<ClothProductMapDetail />} />
      <Route path="edit" element={<ClothProductMapUpdate />} />
      <Route path="delete" element={<ClothProductMapDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ClothProductMapRoutes;
