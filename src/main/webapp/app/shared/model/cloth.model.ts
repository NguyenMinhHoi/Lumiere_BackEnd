import dayjs from 'dayjs';
import { ClothStatus } from 'app/shared/model/enumerations/cloth-status.model';

export interface ICloth {
  id?: number;
  code?: string;
  name?: string;
  material?: string | null;
  color?: string | null;
  width?: number | null;
  length?: number | null;
  unit?: string | null;
  status?: keyof typeof ClothStatus;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<ICloth> = {};
