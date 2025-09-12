import dayjs from 'dayjs';

export interface ISurveyResponse {
  id?: number;
  surveyId?: number;
  customerId?: number | null;
  ticketId?: number | null;
  respondedAt?: dayjs.Dayjs;
  score?: number | null;
  comment?: string | null;
}

export const defaultValue: Readonly<ISurveyResponse> = {};
