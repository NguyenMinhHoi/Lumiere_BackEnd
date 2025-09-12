import dayjs from 'dayjs';

export interface IAttachment {
  id?: number;
  ticketId?: number | null;
  commentId?: number | null;
  name?: string;
  url?: string;
  contentType?: string | null;
  size?: number | null;
  uploadedAt?: dayjs.Dayjs;
}

export const defaultValue: Readonly<IAttachment> = {};
