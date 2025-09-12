export interface IOrderItem {
  id?: number;
  orderId?: number;
  variantId?: number;
  quantity?: number;
  unitPrice?: number;
  totalPrice?: number;
  nameSnapshot?: string | null;
  skuSnapshot?: string | null;
}

export const defaultValue: Readonly<IOrderItem> = {};
