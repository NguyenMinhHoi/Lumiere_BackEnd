import dayjs from 'dayjs';
import { ITicket } from 'app/shared/model/ticket.model';
import { IUser } from 'app/shared/model/user.model';
import { StorageType } from 'app/shared/model/enumerations/storage-type.model';
import { FileStatus } from 'app/shared/model/enumerations/file-status.model';

export interface ITicketFile {
  id?: number;
  fileName?: string;
  originalName?: string | null;
  contentType?: string | null;
  capacity?: number;
  storageType?: keyof typeof StorageType;
  path?: string | null;
  url?: string | null;
  checksum?: string | null;
  status?: keyof typeof FileStatus;
  uploadedAt?: dayjs.Dayjs;
  ticket?: ITicket | null;
  uploader?: IUser | null;
}

export const defaultValue: Readonly<ITicketFile> = {};
