import dayjs from 'dayjs';

export interface ICart {
  id?: number;
  customerId?: number;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<ICart> = {};
