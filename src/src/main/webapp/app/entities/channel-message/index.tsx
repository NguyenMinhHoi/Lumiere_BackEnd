import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ChannelMessage from './channel-message';
import ChannelMessageDetail from './channel-message-detail';
import ChannelMessageUpdate from './channel-message-update';
import ChannelMessageDeleteDialog from './channel-message-delete-dialog';

const ChannelMessageRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ChannelMessage />} />
    <Route path="new" element={<ChannelMessageUpdate />} />
    <Route path=":id">
      <Route index element={<ChannelMessageDetail />} />
      <Route path="edit" element={<ChannelMessageUpdate />} />
      <Route path="delete" element={<ChannelMessageDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ChannelMessageRoutes;
