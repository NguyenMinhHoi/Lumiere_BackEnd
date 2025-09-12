import dayjs from 'dayjs';

export interface IInventory {
  id?: number;
  productVariantId?: number;
  warehouseId?: number;
  quantity?: number;
  updatedAt?: dayjs.Dayjs;
}

export const defaultValue: Readonly<IInventory> = {};
