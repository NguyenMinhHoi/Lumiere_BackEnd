import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Survey from './survey';
import SurveyDetail from './survey-detail';
import SurveyUpdate from './survey-update';
import SurveyDeleteDialog from './survey-delete-dialog';

const SurveyRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Survey />} />
    <Route path="new" element={<SurveyUpdate />} />
    <Route path=":id">
      <Route index element={<SurveyDetail />} />
      <Route path="edit" element={<SurveyUpdate />} />
      <Route path="delete" element={<SurveyDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SurveyRoutes;
