import dayjs from 'dayjs';

export interface IVoucherRedemption {
  id?: number;
  voucherId?: number;
  orderId?: number;
  customerId?: number;
  redeemedAt?: dayjs.Dayjs;
}

export const defaultValue: Readonly<IVoucherRedemption> = {};
