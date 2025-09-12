import dayjs from 'dayjs';

export interface ICompanyConfigAdditional {
  id?: number;
  companyConfigId?: number;
  configKey?: string;
  configValue?: string | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<ICompanyConfigAdditional> = {};
