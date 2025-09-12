import dayjs from 'dayjs';
import { SurveyType } from 'app/shared/model/enumerations/survey-type.model';

export interface ISurvey {
  id?: number;
  customerId?: number | null;
  surveyType?: keyof typeof SurveyType;
  title?: string;
  sentAt?: dayjs.Dayjs | null;
  dueAt?: dayjs.Dayjs | null;
  isActive?: boolean;
}

export const defaultValue: Readonly<ISurvey> = {
  isActive: false,
};
