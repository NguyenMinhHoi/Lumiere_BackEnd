import dayjs from 'dayjs';
import { IProduct } from 'app/shared/model/product.model';

export interface IProductVariant {
  id?: number;
  sku?: string;
  name?: string;
  price?: number;
  compareAtPrice?: number | null;
  currency?: string | null;
  stockQuantity?: number;
  weight?: number | null;
  length?: number | null;
  width?: number | null;
  height?: number | null;
  isDefault?: boolean;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs | null;
  product?: IProduct | null;
}

export const defaultValue: Readonly<IProductVariant> = {
  isDefault: false,
};
