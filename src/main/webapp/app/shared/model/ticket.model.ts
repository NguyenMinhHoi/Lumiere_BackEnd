import dayjs from 'dayjs';
import { ICustomer } from 'app/shared/model/customer.model';
import { IUser } from 'app/shared/model/user.model';
import { ISlaPlan } from 'app/shared/model/sla-plan.model';
import { IOrders } from 'app/shared/model/orders.model';
import { ITag } from 'app/shared/model/tag.model';
import { TicketStatus } from 'app/shared/model/enumerations/ticket-status.model';
import { Priority } from 'app/shared/model/enumerations/priority.model';
import { ChannelType } from 'app/shared/model/enumerations/channel-type.model';

export interface ITicket {
  id?: number;
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
  customer?: ICustomer | null;
  assignee?: IUser | null;
  slaPlan?: ISlaPlan | null;
  order?: IOrders | null;
  tags?: ITag[] | null;
}

export const defaultValue: Readonly<ITicket> = {};
