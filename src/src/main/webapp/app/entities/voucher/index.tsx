import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Voucher from './voucher';
import VoucherDetail from './voucher-detail';
import VoucherUpdate from './voucher-update';
import VoucherDeleteDialog from './voucher-delete-dialog';

const VoucherRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Voucher />} />
    <Route path="new" element={<VoucherUpdate />} />
    <Route path=":id">
      <Route index element={<VoucherDetail />} />
      <Route path="edit" element={<VoucherUpdate />} />
      <Route path="delete" element={<VoucherDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default VoucherRoutes;
