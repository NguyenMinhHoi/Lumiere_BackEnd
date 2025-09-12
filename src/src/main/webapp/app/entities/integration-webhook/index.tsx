import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import IntegrationWebhook from './integration-webhook';
import IntegrationWebhookDetail from './integration-webhook-detail';
import IntegrationWebhookUpdate from './integration-webhook-update';
import IntegrationWebhookDeleteDialog from './integration-webhook-delete-dialog';

const IntegrationWebhookRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<IntegrationWebhook />} />
    <Route path="new" element={<IntegrationWebhookUpdate />} />
    <Route path=":id">
      <Route index element={<IntegrationWebhookDetail />} />
      <Route path="edit" element={<IntegrationWebhookUpdate />} />
      <Route path="delete" element={<IntegrationWebhookDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default IntegrationWebhookRoutes;
