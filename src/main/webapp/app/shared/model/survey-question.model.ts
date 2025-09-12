import { QuestionType } from 'app/shared/model/enumerations/question-type.model';

export interface ISurveyQuestion {
  id?: number;
  surveyId?: number;
  text?: string;
  questionType?: keyof typeof QuestionType;
  scaleMin?: number | null;
  scaleMax?: number | null;
  isNeed?: boolean;
  orderNo?: number;
}

export const defaultValue: Readonly<ISurveyQuestion> = {
  isNeed: false,
};
