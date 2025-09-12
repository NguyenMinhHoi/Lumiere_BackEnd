import dayjs from 'dayjs';

export interface IProductVariant {
  id?: number;
  productId?: number;
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
}

export const defaultValue: Readonly<IProductVariant> = {
  isDefault: false,
};
