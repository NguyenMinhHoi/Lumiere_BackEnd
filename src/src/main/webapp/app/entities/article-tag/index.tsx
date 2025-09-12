import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ArticleTag from './article-tag';
import ArticleTagDetail from './article-tag-detail';
import ArticleTagUpdate from './article-tag-update';
import ArticleTagDeleteDialog from './article-tag-delete-dialog';

const ArticleTagRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ArticleTag />} />
    <Route path="new" element={<ArticleTagUpdate />} />
    <Route path=":id">
      <Route index element={<ArticleTagDetail />} />
      <Route path="edit" element={<ArticleTagUpdate />} />
      <Route path="delete" element={<ArticleTagDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ArticleTagRoutes;
