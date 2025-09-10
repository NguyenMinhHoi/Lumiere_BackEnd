import dayjs from 'dayjs';
import { SupplierStatus } from 'app/shared/model/enumerations/supplier-status.model';

export interface ISupplier {
  id?: number;
  code?: string;
  name?: string;
  email?: string | null;
  phone?: string | null;
  address?: string | null;
  status?: keyof typeof SupplierStatus;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<ISupplier> = {};
