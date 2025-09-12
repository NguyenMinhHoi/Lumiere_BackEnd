import dayjs from 'dayjs';
import { MessageDirection } from 'app/shared/model/enumerations/message-direction.model';

export interface IChannelMessage {
  id?: number;
  ticketId?: number;
  authorId?: number | null;
  direction?: keyof typeof MessageDirection;
  content?: string;
  sentAt?: dayjs.Dayjs;
  externalMessageId?: string | null;
}

export const defaultValue: Readonly<IChannelMessage> = {};
