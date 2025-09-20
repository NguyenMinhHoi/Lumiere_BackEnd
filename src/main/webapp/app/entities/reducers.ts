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
import ticketTag from 'app/entities/ticket-tag/ticket-tag.reducer';
import articleTag from 'app/entities/article-tag/article-tag.reducer';
import appConfig from 'app/entities/app-config/app-config.reducer';
import companyConfig from 'app/entities/company-config/company-config.reducer';
import companyConfigAdditional from 'app/entities/company-config-additional/company-config-additional.reducer';
import voucher from 'app/entities/voucher/voucher.reducer';
import voucherRedemption from 'app/entities/voucher-redemption/voucher-redemption.reducer';
import cart from 'app/entities/cart/cart.reducer';
import cartItem from 'app/entities/cart-item/cart-item.reducer';
import warehouse from 'app/entities/warehouse/warehouse.reducer';
import inventory from 'app/entities/inventory/inventory.reducer';
import stockMovement from 'app/entities/stock-movement/stock-movement.reducer';
import campaign from 'app/entities/campaign/campaign.reducer';
import integrationLog from 'app/entities/integration-log/integration-log.reducer';
import employee from 'app/entities/employee/employee.reducer';
import cloth from 'app/entities/cloth/cloth.reducer';
import clothInventory from 'app/entities/cloth-inventory/cloth-inventory.reducer';
import clothStockMovement from 'app/entities/cloth-stock-movement/cloth-stock-movement.reducer';
import clothSupplement from 'app/entities/cloth-supplement/cloth-supplement.reducer';
import clothProductMap from 'app/entities/cloth-product-map/cloth-product-map.reducer';
import clothAudit from 'app/entities/cloth-audit/cloth-audit.reducer';
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
  ticketTag,
  articleTag,
  appConfig,
  companyConfig,
  companyConfigAdditional,
  voucher,
  voucherRedemption,
  cart,
  cartItem,
  warehouse,
  inventory,
  stockMovement,
  campaign,
  integrationLog,
  employee,
  cloth,
  clothInventory,
  clothStockMovement,
  clothSupplement,
  clothProductMap,
  clothAudit,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
