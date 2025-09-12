import dayjs from 'dayjs';
import { StockMovementReason } from 'app/shared/model/enumerations/stock-movement-reason.model';

export interface IStockMovement {
  id?: number;
  productVariantId?: number;
  warehouseId?: number;
  delta?: number;
  reason?: keyof typeof StockMovementReason;
  refOrderId?: number | null;
  createdAt?: dayjs.Dayjs;
}

export const defaultValue: Readonly<IStockMovement> = {};
