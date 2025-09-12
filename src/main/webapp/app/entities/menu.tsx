import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/customer">
        <Translate contentKey="global.menu.entities.customer" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/ticket">
        <Translate contentKey="global.menu.entities.ticket" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/ticket-comment">
        <Translate contentKey="global.menu.entities.ticketComment" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/attachment">
        <Translate contentKey="global.menu.entities.attachment" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/channel-message">
        <Translate contentKey="global.menu.entities.channelMessage" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/knowledge-category">
        <Translate contentKey="global.menu.entities.knowledgeCategory" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/knowledge-article">
        <Translate contentKey="global.menu.entities.knowledgeArticle" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/tag">
        <Translate contentKey="global.menu.entities.tag" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/sla-plan">
        <Translate contentKey="global.menu.entities.slaPlan" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/survey">
        <Translate contentKey="global.menu.entities.survey" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/survey-question">
        <Translate contentKey="global.menu.entities.surveyQuestion" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/survey-response">
        <Translate contentKey="global.menu.entities.surveyResponse" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/notification">
        <Translate contentKey="global.menu.entities.notification" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/integration-webhook">
        <Translate contentKey="global.menu.entities.integrationWebhook" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/ticket-file">
        <Translate contentKey="global.menu.entities.ticketFile" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/product">
        <Translate contentKey="global.menu.entities.product" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/product-variant">
        <Translate contentKey="global.menu.entities.productVariant" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/orders">
        <Translate contentKey="global.menu.entities.orders" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/order-item">
        <Translate contentKey="global.menu.entities.orderItem" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/supplier">
        <Translate contentKey="global.menu.entities.supplier" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/supplement">
        <Translate contentKey="global.menu.entities.supplement" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/audit-history">
        <Translate contentKey="global.menu.entities.auditHistory" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/ticket-tag">
        <Translate contentKey="global.menu.entities.ticketTag" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/article-tag">
        <Translate contentKey="global.menu.entities.articleTag" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/app-config">
        <Translate contentKey="global.menu.entities.appConfig" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/company-config">
        <Translate contentKey="global.menu.entities.companyConfig" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/company-config-additional">
        <Translate contentKey="global.menu.entities.companyConfigAdditional" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/voucher">
        <Translate contentKey="global.menu.entities.voucher" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/voucher-redemption">
        <Translate contentKey="global.menu.entities.voucherRedemption" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/cart">
        <Translate contentKey="global.menu.entities.cart" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/cart-item">
        <Translate contentKey="global.menu.entities.cartItem" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/warehouse">
        <Translate contentKey="global.menu.entities.warehouse" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/inventory">
        <Translate contentKey="global.menu.entities.inventory" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/stock-movement">
        <Translate contentKey="global.menu.entities.stockMovement" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/campaign">
        <Translate contentKey="global.menu.entities.campaign" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/integration-log">
        <Translate contentKey="global.menu.entities.integrationLog" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/employee">
        <Translate contentKey="global.menu.entities.employee" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
