import dayjs from 'dayjs';
import { StockMovementReason } from 'app/shared/model/enumerations/stock-movement-reason.model';

export interface IClothStockMovement {
  id?: number;
  clothId?: number;
  warehouseId?: number;
  delta?: number;
  reason?: keyof typeof StockMovementReason;
  refOrderId?: number | null;
  createdAt?: dayjs.Dayjs;
}

export const defaultValue: Readonly<IClothStockMovement> = {};
