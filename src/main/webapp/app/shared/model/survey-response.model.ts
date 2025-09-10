import dayjs from 'dayjs';
import { ISurvey } from 'app/shared/model/survey.model';
import { ICustomer } from 'app/shared/model/customer.model';
import { ITicket } from 'app/shared/model/ticket.model';

export interface ISurveyResponse {
  id?: number;
  respondedAt?: dayjs.Dayjs;
  score?: number | null;
  comment?: string | null;
  survey?: ISurvey | null;
  customer?: ICustomer | null;
  ticket?: ITicket | null;
}

export const defaultValue: Readonly<ISurveyResponse> = {};
