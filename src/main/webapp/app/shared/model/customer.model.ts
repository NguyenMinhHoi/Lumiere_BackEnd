import dayjs from 'dayjs';
import { CustomerTier } from 'app/shared/model/enumerations/customer-tier.model';

export interface ICustomer {
  id?: number;
  code?: string;
  fullName?: string;
  email?: string;
  phone?: string | null;
  tier?: keyof typeof CustomerTier;
  points?: number;
  dob?: dayjs.Dayjs | null;
  address?: string | null;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<ICustomer> = {};
