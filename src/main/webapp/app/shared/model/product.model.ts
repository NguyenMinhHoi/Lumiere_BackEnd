import dayjs from 'dayjs';
import { ProductStatus } from 'app/shared/model/enumerations/product-status.model';

export interface IProduct {
  id?: number;
  code?: string;
  name?: string;
  slug?: string | null;
  description?: string | null;
  status?: keyof typeof ProductStatus;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<IProduct> = {};
