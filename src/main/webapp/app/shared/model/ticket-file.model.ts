import dayjs from 'dayjs';
import { StorageType } from 'app/shared/model/enumerations/storage-type.model';
import { FileStatus } from 'app/shared/model/enumerations/file-status.model';

export interface ITicketFile {
  id?: number;
  ticketId?: number;
  uploaderId?: number | null;
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
}

export const defaultValue: Readonly<ITicketFile> = {};
