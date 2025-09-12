import dayjs from 'dayjs';
import { OrderStatus } from 'app/shared/model/enumerations/order-status.model';
import { PaymentStatus } from 'app/shared/model/enumerations/payment-status.model';
import { FulfillmentStatus } from 'app/shared/model/enumerations/fulfillment-status.model';

export interface IOrders {
  id?: number;
  customerId?: number | null;
  code?: string;
  status?: keyof typeof OrderStatus;
  paymentStatus?: keyof typeof PaymentStatus;
  fulfillmentStatus?: keyof typeof FulfillmentStatus;
  totalAmount?: number;
  currency?: string | null;
  note?: string | null;
  placedAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<IOrders> = {};
