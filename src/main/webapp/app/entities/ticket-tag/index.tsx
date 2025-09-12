import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TicketTag from './ticket-tag';
import TicketTagDetail from './ticket-tag-detail';
import TicketTagUpdate from './ticket-tag-update';
import TicketTagDeleteDialog from './ticket-tag-delete-dialog';

const TicketTagRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TicketTag />} />
    <Route path="new" element={<TicketTagUpdate />} />
    <Route path=":id">
      <Route index element={<TicketTagDetail />} />
      <Route path="edit" element={<TicketTagUpdate />} />
      <Route path="delete" element={<TicketTagDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TicketTagRoutes;
