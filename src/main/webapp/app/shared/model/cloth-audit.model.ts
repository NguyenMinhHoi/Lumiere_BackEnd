import dayjs from 'dayjs';
import { AuditAction } from 'app/shared/model/enumerations/audit-action.model';

export interface IClothAudit {
  id?: number;
  clothId?: number;
  supplierId?: number;
  productId?: number;
  action?: keyof typeof AuditAction;
  quantity?: number;
  unit?: string | null;
  sentAt?: dayjs.Dayjs;
  note?: string | null;
  createdAt?: dayjs.Dayjs;
}

export const defaultValue: Readonly<IClothAudit> = {};
