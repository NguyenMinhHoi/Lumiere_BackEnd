import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TicketComment from './ticket-comment';
import TicketCommentDetail from './ticket-comment-detail';
import TicketCommentUpdate from './ticket-comment-update';
import TicketCommentDeleteDialog from './ticket-comment-delete-dialog';

const TicketCommentRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TicketComment />} />
    <Route path="new" element={<TicketCommentUpdate />} />
    <Route path=":id">
      <Route index element={<TicketCommentDetail />} />
      <Route path="edit" element={<TicketCommentUpdate />} />
      <Route path="delete" element={<TicketCommentDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TicketCommentRoutes;
