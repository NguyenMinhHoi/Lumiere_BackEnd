import dayjs from 'dayjs';

export interface IAppConfig {
  id?: number;
  appCode?: string;
  configKey?: string;
  configValue?: string | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<IAppConfig> = {};
