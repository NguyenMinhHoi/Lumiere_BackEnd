import dayjs from 'dayjs';

export interface IClothProductMap {
  id?: number;
  clothId?: number;
  productId?: number;
  quantity?: number;
  unit?: string | null;
  note?: string | null;
  createdAt?: dayjs.Dayjs;
}

export const defaultValue: Readonly<IClothProductMap> = {};
