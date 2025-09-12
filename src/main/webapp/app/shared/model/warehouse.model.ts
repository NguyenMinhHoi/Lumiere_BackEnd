import dayjs from 'dayjs';

export interface IWarehouse {
  id?: number;
  code?: string;
  name?: string;
  address?: string | null;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<IWarehouse> = {};
