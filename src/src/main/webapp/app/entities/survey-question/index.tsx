import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import SurveyQuestion from './survey-question';
import SurveyQuestionDetail from './survey-question-detail';
import SurveyQuestionUpdate from './survey-question-update';
import SurveyQuestionDeleteDialog from './survey-question-delete-dialog';

const SurveyQuestionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<SurveyQuestion />} />
    <Route path="new" element={<SurveyQuestionUpdate />} />
    <Route path=":id">
      <Route index element={<SurveyQuestionDetail />} />
      <Route path="edit" element={<SurveyQuestionUpdate />} />
      <Route path="delete" element={<SurveyQuestionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SurveyQuestionRoutes;
