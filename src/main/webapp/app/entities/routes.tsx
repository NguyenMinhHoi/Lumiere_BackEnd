import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Customer from './customer';
import Ticket from './ticket';
import TicketComment from './ticket-comment';
import Attachment from './attachment';
import ChannelMessage from './channel-message';
import KnowledgeCategory from './knowledge-category';
import KnowledgeArticle from './knowledge-article';
import Tag from './tag';
import SlaPlan from './sla-plan';
import Survey from './survey';
import SurveyQuestion from './survey-question';
import SurveyResponse from './survey-response';
import Notification from './notification';
import IntegrationWebhook from './integration-webhook';
import TicketFile from './ticket-file';
import Product from './product';
import ProductVariant from './product-variant';
import Orders from './orders';
import OrderItem from './order-item';
import Supplier from './supplier';
import Supplement from './supplement';
import AuditHistory from './audit-history';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="customer/*" element={<Customer />} />
        <Route path="ticket/*" element={<Ticket />} />
        <Route path="ticket-comment/*" element={<TicketComment />} />
        <Route path="attachment/*" element={<Attachment />} />
        <Route path="channel-message/*" element={<ChannelMessage />} />
        <Route path="knowledge-category/*" element={<KnowledgeCategory />} />
        <Route path="knowledge-article/*" element={<KnowledgeArticle />} />
        <Route path="tag/*" element={<Tag />} />
        <Route path="sla-plan/*" element={<SlaPlan />} />
        <Route path="survey/*" element={<Survey />} />
        <Route path="survey-question/*" element={<SurveyQuestion />} />
        <Route path="survey-response/*" element={<SurveyResponse />} />
        <Route path="notification/*" element={<Notification />} />
        <Route path="integration-webhook/*" element={<IntegrationWebhook />} />
        <Route path="ticket-file/*" element={<TicketFile />} />
        <Route path="product/*" element={<Product />} />
        <Route path="product-variant/*" element={<ProductVariant />} />
        <Route path="orders/*" element={<Orders />} />
        <Route path="order-item/*" element={<OrderItem />} />
        <Route path="supplier/*" element={<Supplier />} />
        <Route path="supplement/*" element={<Supplement />} />
        <Route path="audit-history/*" element={<AuditHistory />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
