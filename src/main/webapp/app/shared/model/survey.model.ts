import dayjs from 'dayjs';
import { ICustomer } from 'app/shared/model/customer.model';
import { SurveyType } from 'app/shared/model/enumerations/survey-type.model';

export interface ISurvey {
  id?: number;
  surveyType?: keyof typeof SurveyType;
  title?: string;
  sentAt?: dayjs.Dayjs | null;
  dueAt?: dayjs.Dayjs | null;
  isActive?: boolean;
  customer?: ICustomer | null;
}

export const defaultValue: Readonly<ISurvey> = {
  isActive: false,
};
