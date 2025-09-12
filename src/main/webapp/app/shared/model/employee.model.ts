import dayjs from 'dayjs';
import { EmployeeRole } from 'app/shared/model/enumerations/employee-role.model';
import { EmployeeStatus } from 'app/shared/model/enumerations/employee-status.model';

export interface IEmployee {
  id?: number;
  code?: string;
  fullName?: string;
  email?: string;
  phone?: string | null;
  role?: keyof typeof EmployeeRole;
  status?: keyof typeof EmployeeStatus;
  department?: string | null;
  joinedAt?: dayjs.Dayjs | null;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<IEmployee> = {};
