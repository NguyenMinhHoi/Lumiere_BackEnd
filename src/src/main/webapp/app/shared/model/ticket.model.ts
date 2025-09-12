import dayjs from 'dayjs';
import { TicketStatus } from 'app/shared/model/enumerations/ticket-status.model';
import { Priority } from 'app/shared/model/enumerations/priority.model';
import { ChannelType } from 'app/shared/model/enumerations/channel-type.model';

export interface ITicket {
  id?: number;
  customerId?: number;
  slaPlanId?: number | null;
  orderId?: number | null;
  assigneeEmployeeId?: number | null;
  code?: string;
  subject?: string;
  description?: string | null;
  status?: keyof typeof TicketStatus;
  priority?: keyof typeof Priority;
  channel?: keyof typeof ChannelType;
  openedAt?: dayjs.Dayjs;
  firstResponseAt?: dayjs.Dayjs | null;
  resolvedAt?: dayjs.Dayjs | null;
  slaDueAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<ITicket> = {};
