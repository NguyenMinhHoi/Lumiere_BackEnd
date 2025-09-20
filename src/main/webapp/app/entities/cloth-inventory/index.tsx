import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ClothInventory from './cloth-inventory';
import ClothInventoryDetail from './cloth-inventory-detail';
import ClothInventoryUpdate from './cloth-inventory-update';
import ClothInventoryDeleteDialog from './cloth-inventory-delete-dialog';

const ClothInventoryRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ClothInventory />} />
    <Route path="new" element={<ClothInventoryUpdate />} />
    <Route path=":id">
      <Route index element={<ClothInventoryDetail />} />
      <Route path="edit" element={<ClothInventoryUpdate />} />
      <Route path="delete" element={<ClothInventoryDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ClothInventoryRoutes;
