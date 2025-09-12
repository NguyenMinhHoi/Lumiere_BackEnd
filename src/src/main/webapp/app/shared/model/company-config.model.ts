import dayjs from 'dayjs';

export interface ICompanyConfig {
  id?: number;
  companyId?: number;
  appId?: number;
  isActive?: boolean | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<ICompanyConfig> = {
  isActive: false,
};
