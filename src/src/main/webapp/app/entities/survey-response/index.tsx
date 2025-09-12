import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import SurveyResponse from './survey-response';
import SurveyResponseDetail from './survey-response-detail';
import SurveyResponseUpdate from './survey-response-update';
import SurveyResponseDeleteDialog from './survey-response-delete-dialog';

const SurveyResponseRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<SurveyResponse />} />
    <Route path="new" element={<SurveyResponseUpdate />} />
    <Route path=":id">
      <Route index element={<SurveyResponseDetail />} />
      <Route path="edit" element={<SurveyResponseUpdate />} />
      <Route path="delete" element={<SurveyResponseDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SurveyResponseRoutes;
