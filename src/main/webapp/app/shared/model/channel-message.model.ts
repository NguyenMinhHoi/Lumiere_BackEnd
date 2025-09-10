import dayjs from 'dayjs';
import { ITicket } from 'app/shared/model/ticket.model';
import { IUser } from 'app/shared/model/user.model';
import { MessageDirection } from 'app/shared/model/enumerations/message-direction.model';

export interface IChannelMessage {
  id?: number;
  direction?: keyof typeof MessageDirection;
  content?: string;
  sentAt?: dayjs.Dayjs;
  externalMessageId?: string | null;
  ticket?: ITicket | null;
  author?: IUser | null;
}

export const defaultValue: Readonly<IChannelMessage> = {};
