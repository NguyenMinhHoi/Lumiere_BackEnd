import dayjs from 'dayjs';

export interface IClothSupplement {
  id?: number;
  clothId?: number;
  supplierId?: number;
  supplyPrice?: number;
  currency?: string | null;
  leadTimeDays?: number | null;
  minOrderQty?: number | null;
  isPreferred?: boolean;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<IClothSupplement> = {
  isPreferred: false,
};
