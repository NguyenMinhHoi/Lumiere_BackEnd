import dayjs from 'dayjs';
import { ITicket } from 'app/shared/model/ticket.model';
import { IUser } from 'app/shared/model/user.model';
import { Visibility } from 'app/shared/model/enumerations/visibility.model';

export interface ITicketComment {
  id?: number;
  body?: string;
  visibility?: keyof typeof Visibility;
  createdAt?: dayjs.Dayjs;
  ticket?: ITicket | null;
  author?: IUser | null;
}

export const defaultValue: Readonly<ITicketComment> = {};
