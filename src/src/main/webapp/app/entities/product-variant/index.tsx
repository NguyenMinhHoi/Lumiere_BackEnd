import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ProductVariant from './product-variant';
import ProductVariantDetail from './product-variant-detail';
import ProductVariantUpdate from './product-variant-update';
import ProductVariantDeleteDialog from './product-variant-delete-dialog';

const ProductVariantRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ProductVariant />} />
    <Route path="new" element={<ProductVariantUpdate />} />
    <Route path=":id">
      <Route index element={<ProductVariantDetail />} />
      <Route path="edit" element={<ProductVariantUpdate />} />
      <Route path="delete" element={<ProductVariantDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ProductVariantRoutes;
