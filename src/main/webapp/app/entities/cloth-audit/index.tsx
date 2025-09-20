import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ClothAudit from './cloth-audit';
import ClothAuditDetail from './cloth-audit-detail';
import ClothAuditUpdate from './cloth-audit-update';
import ClothAuditDeleteDialog from './cloth-audit-delete-dialog';

const ClothAuditRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ClothAudit />} />
    <Route path="new" element={<ClothAuditUpdate />} />
    <Route path=":id">
      <Route index element={<ClothAuditDetail />} />
      <Route path="edit" element={<ClothAuditUpdate />} />
      <Route path="delete" element={<ClothAuditDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ClothAuditRoutes;
