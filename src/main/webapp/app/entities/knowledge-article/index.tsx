import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import KnowledgeArticle from './knowledge-article';
import KnowledgeArticleDetail from './knowledge-article-detail';
import KnowledgeArticleUpdate from './knowledge-article-update';
import KnowledgeArticleDeleteDialog from './knowledge-article-delete-dialog';

const KnowledgeArticleRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<KnowledgeArticle />} />
    <Route path="new" element={<KnowledgeArticleUpdate />} />
    <Route path=":id">
      <Route index element={<KnowledgeArticleDetail />} />
      <Route path="edit" element={<KnowledgeArticleUpdate />} />
      <Route path="delete" element={<KnowledgeArticleDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default KnowledgeArticleRoutes;
