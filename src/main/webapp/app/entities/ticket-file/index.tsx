import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TicketFile from './ticket-file';
import TicketFileDetail from './ticket-file-detail';
import TicketFileUpdate from './ticket-file-update';
import TicketFileDeleteDialog from './ticket-file-delete-dialog';

const TicketFileRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TicketFile />} />
    <Route path="new" element={<TicketFileUpdate />} />
    <Route path=":id">
      <Route index element={<TicketFileDetail />} />
      <Route path="edit" element={<TicketFileUpdate />} />
      <Route path="delete" element={<TicketFileDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TicketFileRoutes;
