import customer from 'app/entities/customer/customer.reducer';
import ticket from 'app/entities/ticket/ticket.reducer';
import ticketComment from 'app/entities/ticket-comment/ticket-comment.reducer';
import attachment from 'app/entities/attachment/attachment.reducer';
import channelMessage from 'app/entities/channel-message/channel-message.reducer';
import knowledgeCategory from 'app/entities/knowledge-category/knowledge-category.reducer';
import knowledgeArticle from 'app/entities/knowledge-article/knowledge-article.reducer';
import tag from 'app/entities/tag/tag.reducer';
import slaPlan from 'app/entities/sla-plan/sla-plan.reducer';
import survey from 'app/entities/survey/survey.reducer';
import surveyQuestion from 'app/entities/survey-question/survey-question.reducer';
import surveyResponse from 'app/entities/survey-response/survey-response.reducer';
import notification from 'app/entities/notification/notification.reducer';
import integrationWebhook from 'app/entities/integration-webhook/integration-webhook.reducer';
import ticketFile from 'app/entities/ticket-file/ticket-file.reducer';
import product from 'app/entities/product/product.reducer';
import productVariant from 'app/entities/product-variant/product-variant.reducer';
import orders from 'app/entities/orders/orders.reducer';
import orderItem from 'app/entities/order-item/order-item.reducer';
import supplier from 'app/entities/supplier/supplier.reducer';
import supplement from 'app/entities/supplement/supplement.reducer';
import auditHistory from 'app/entities/audit-history/audit-history.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  customer,
  ticket,
  ticketComment,
  attachment,
  channelMessage,
  knowledgeCategory,
  knowledgeArticle,
  tag,
  slaPlan,
  survey,
  surveyQuestion,
  surveyResponse,
  notification,
  integrationWebhook,
  ticketFile,
  product,
  productVariant,
  orders,
  orderItem,
  supplier,
  supplement,
  auditHistory,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
