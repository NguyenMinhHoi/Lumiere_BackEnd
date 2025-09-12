import dayjs from 'dayjs';
import { Visibility } from 'app/shared/model/enumerations/visibility.model';

export interface ITicketComment {
  id?: number;
  ticketId?: number;
  authorId?: number;
  body?: string;
  visibility?: keyof typeof Visibility;
  createdAt?: dayjs.Dayjs;
}

export const defaultValue: Readonly<ITicketComment> = {};
