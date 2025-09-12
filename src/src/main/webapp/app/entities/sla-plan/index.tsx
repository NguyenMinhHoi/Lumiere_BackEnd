import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import SlaPlan from './sla-plan';
import SlaPlanDetail from './sla-plan-detail';
import SlaPlanUpdate from './sla-plan-update';
import SlaPlanDeleteDialog from './sla-plan-delete-dialog';

const SlaPlanRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<SlaPlan />} />
    <Route path="new" element={<SlaPlanUpdate />} />
    <Route path=":id">
      <Route index element={<SlaPlanDetail />} />
      <Route path="edit" element={<SlaPlanUpdate />} />
      <Route path="delete" element={<SlaPlanDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SlaPlanRoutes;
