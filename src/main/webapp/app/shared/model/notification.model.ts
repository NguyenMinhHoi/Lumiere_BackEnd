import dayjs from 'dayjs';
import { ITicket } from 'app/shared/model/ticket.model';
import { ICustomer } from 'app/shared/model/customer.model';
import { ISurvey } from 'app/shared/model/survey.model';
import { NotificationType } from 'app/shared/model/enumerations/notification-type.model';
import { DeliveryChannel } from 'app/shared/model/enumerations/delivery-channel.model';
import { SendStatus } from 'app/shared/model/enumerations/send-status.model';

export interface INotification {
  id?: number;
  type?: keyof typeof NotificationType;
  channel?: keyof typeof DeliveryChannel;
  subject?: string | null;
  payload?: string;
  sendStatus?: keyof typeof SendStatus;
  retryCount?: number;
  lastTriedAt?: dayjs.Dayjs | null;
  createdAt?: dayjs.Dayjs;
  ticket?: ITicket | null;
  customer?: ICustomer | null;
  survey?: ISurvey | null;
}

export const defaultValue: Readonly<INotification> = {};
