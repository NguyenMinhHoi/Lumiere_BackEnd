import dayjs from 'dayjs';
import { VoucherType } from 'app/shared/model/enumerations/voucher-type.model';
import { VoucherStatus } from 'app/shared/model/enumerations/voucher-status.model';

export interface IVoucher {
  id?: number;
  code?: string;
  discountType?: keyof typeof VoucherType;
  discountValue?: number;
  minOrderValue?: number | null;
  maxDiscountValue?: number | null;
  usageLimit?: number | null;
  usedCount?: number;
  validFrom?: dayjs.Dayjs;
  validTo?: dayjs.Dayjs;
  status?: keyof typeof VoucherStatus;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<IVoucher> = {};
