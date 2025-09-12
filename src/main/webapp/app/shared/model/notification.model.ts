import dayjs from 'dayjs';
import { NotificationType } from 'app/shared/model/enumerations/notification-type.model';
import { DeliveryChannel } from 'app/shared/model/enumerations/delivery-channel.model';
import { SendStatus } from 'app/shared/model/enumerations/send-status.model';

export interface INotification {
  id?: number;
  ticketId?: number | null;
  customerId?: number | null;
  surveyId?: number | null;
  type?: keyof typeof NotificationType;
  channel?: keyof typeof DeliveryChannel;
  subject?: string | null;
  payload?: string;
  sendStatus?: keyof typeof SendStatus;
  retryCount?: number;
  lastTriedAt?: dayjs.Dayjs | null;
  createdAt?: dayjs.Dayjs;
}

export const defaultValue: Readonly<INotification> = {};
