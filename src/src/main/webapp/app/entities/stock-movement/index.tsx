import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import StockMovement from './stock-movement';
import StockMovementDetail from './stock-movement-detail';
import StockMovementUpdate from './stock-movement-update';
import StockMovementDeleteDialog from './stock-movement-delete-dialog';

const StockMovementRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<StockMovement />} />
    <Route path="new" element={<StockMovementUpdate />} />
    <Route path=":id">
      <Route index element={<StockMovementDetail />} />
      <Route path="edit" element={<StockMovementUpdate />} />
      <Route path="delete" element={<StockMovementDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default StockMovementRoutes;
