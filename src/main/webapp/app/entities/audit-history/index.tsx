import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AuditHistory from './audit-history';
import AuditHistoryDetail from './audit-history-detail';
import AuditHistoryUpdate from './audit-history-update';
import AuditHistoryDeleteDialog from './audit-history-delete-dialog';

const AuditHistoryRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<AuditHistory />} />
    <Route path="new" element={<AuditHistoryUpdate />} />
    <Route path=":id">
      <Route index element={<AuditHistoryDetail />} />
      <Route path="edit" element={<AuditHistoryUpdate />} />
      <Route path="delete" element={<AuditHistoryDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AuditHistoryRoutes;
