import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Supplier from './supplier';
import SupplierDetail from './supplier-detail';
import SupplierUpdate from './supplier-update';
import SupplierDeleteDialog from './supplier-delete-dialog';

const SupplierRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Supplier />} />
    <Route path="new" element={<SupplierUpdate />} />
    <Route path=":id">
      <Route index element={<SupplierDetail />} />
      <Route path="edit" element={<SupplierUpdate />} />
      <Route path="delete" element={<SupplierDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SupplierRoutes;
