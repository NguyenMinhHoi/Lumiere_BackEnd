import { IOrders } from 'app/shared/model/orders.model';
import { IProductVariant } from 'app/shared/model/product-variant.model';

export interface IOrderItem {
  id?: number;
  quantity?: number;
  unitPrice?: number;
  totalPrice?: number;
  nameSnapshot?: string | null;
  skuSnapshot?: string | null;
  order?: IOrders | null;
  variant?: IProductVariant | null;
}

export const defaultValue: Readonly<IOrderItem> = {};
