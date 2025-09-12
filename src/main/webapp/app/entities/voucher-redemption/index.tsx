import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import VoucherRedemption from './voucher-redemption';
import VoucherRedemptionDetail from './voucher-redemption-detail';
import VoucherRedemptionUpdate from './voucher-redemption-update';
import VoucherRedemptionDeleteDialog from './voucher-redemption-delete-dialog';

const VoucherRedemptionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<VoucherRedemption />} />
    <Route path="new" element={<VoucherRedemptionUpdate />} />
    <Route path=":id">
      <Route index element={<VoucherRedemptionDetail />} />
      <Route path="edit" element={<VoucherRedemptionUpdate />} />
      <Route path="delete" element={<VoucherRedemptionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default VoucherRedemptionRoutes;
