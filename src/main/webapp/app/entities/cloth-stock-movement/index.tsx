import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ClothStockMovement from './cloth-stock-movement';
import ClothStockMovementDetail from './cloth-stock-movement-detail';
import ClothStockMovementUpdate from './cloth-stock-movement-update';
import ClothStockMovementDeleteDialog from './cloth-stock-movement-delete-dialog';

const ClothStockMovementRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ClothStockMovement />} />
    <Route path="new" element={<ClothStockMovementUpdate />} />
    <Route path=":id">
      <Route index element={<ClothStockMovementDetail />} />
      <Route path="edit" element={<ClothStockMovementUpdate />} />
      <Route path="delete" element={<ClothStockMovementDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ClothStockMovementRoutes;
