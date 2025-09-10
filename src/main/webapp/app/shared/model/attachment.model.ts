import dayjs from 'dayjs';
import { ITicket } from 'app/shared/model/ticket.model';
import { ITicketComment } from 'app/shared/model/ticket-comment.model';

export interface IAttachment {
  id?: number;
  name?: string;
  url?: string;
  contentType?: string | null;
  size?: number | null;
  uploadedAt?: dayjs.Dayjs;
  ticket?: ITicket | null;
  comment?: ITicketComment | null;
}

export const defaultValue: Readonly<IAttachment> = {};
