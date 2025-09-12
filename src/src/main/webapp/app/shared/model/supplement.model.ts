import dayjs from 'dayjs';

export interface ISupplement {
  id?: number;
  productId?: number;
  supplierId?: number;
  supplyPrice?: number;
  currency?: string | null;
  leadTimeDays?: number | null;
  minOrderQty?: number | null;
  isPreferred?: boolean;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<ISupplement> = {
  isPreferred: false,
};
