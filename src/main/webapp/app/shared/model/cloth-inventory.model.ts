import dayjs from 'dayjs';

export interface IClothInventory {
  id?: number;
  clothId?: number;
  warehouseId?: number;
  quantity?: number;
  updatedAt?: dayjs.Dayjs;
}

export const defaultValue: Readonly<IClothInventory> = {};
