import dayjs from 'dayjs';
import { AuditAction } from 'app/shared/model/enumerations/audit-action.model';

export interface IAuditHistory {
  id?: number;
  entityName?: string;
  entityId?: string;
  action?: keyof typeof AuditAction;
  oldValue?: string | null;
  newValue?: string | null;
  performedBy?: string | null;
  performedAt?: dayjs.Dayjs;
  ipAddress?: string | null;
}

export const defaultValue: Readonly<IAuditHistory> = {};
