import dayjs from 'dayjs';
import { IProduct } from 'app/shared/model/product.model';
import { ISupplier } from 'app/shared/model/supplier.model';

export interface ISupplement {
  id?: number;
  supplyPrice?: number;
  currency?: string | null;
  leadTimeDays?: number | null;
  minOrderQty?: number | null;
  isPreferred?: boolean;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs | null;
  product?: IProduct | null;
  supplier?: ISupplier | null;
}

export const defaultValue: Readonly<ISupplement> = {
  isPreferred: false,
};
