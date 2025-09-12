import dayjs from 'dayjs';

export interface ICartItem {
  id?: number;
  cartId?: number;
  productVariantId?: number;
  quantity?: number;
  addedAt?: dayjs.Dayjs;
}

export const defaultValue: Readonly<ICartItem> = {};
