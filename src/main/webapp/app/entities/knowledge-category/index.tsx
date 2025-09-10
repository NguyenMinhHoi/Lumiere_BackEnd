import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import KnowledgeCategory from './knowledge-category';
import KnowledgeCategoryDetail from './knowledge-category-detail';
import KnowledgeCategoryUpdate from './knowledge-category-update';
import KnowledgeCategoryDeleteDialog from './knowledge-category-delete-dialog';

const KnowledgeCategoryRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<KnowledgeCategory />} />
    <Route path="new" element={<KnowledgeCategoryUpdate />} />
    <Route path=":id">
      <Route index element={<KnowledgeCategoryDetail />} />
      <Route path="edit" element={<KnowledgeCategoryUpdate />} />
      <Route path="delete" element={<KnowledgeCategoryDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default KnowledgeCategoryRoutes;
