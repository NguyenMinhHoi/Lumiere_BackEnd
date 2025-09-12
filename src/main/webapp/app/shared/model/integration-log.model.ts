import dayjs from 'dayjs';
import { AppType } from 'app/shared/model/enumerations/app-type.model';
import { IntegrationStatus } from 'app/shared/model/enumerations/integration-status.model';

export interface IIntegrationLog {
  id?: number;
  sourceApp?: keyof typeof AppType;
  targetApp?: keyof typeof AppType;
  payload?: string;
  response?: string | null;
  status?: keyof typeof IntegrationStatus;
  retries?: number;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<IIntegrationLog> = {};
