import { ISurvey } from 'app/shared/model/survey.model';
import { QuestionType } from 'app/shared/model/enumerations/question-type.model';

export interface ISurveyQuestion {
  id?: number;
  text?: string;
  questionType?: keyof typeof QuestionType;
  scaleMin?: number | null;
  scaleMax?: number | null;
  isNeed?: boolean;
  orderNo?: number;
  survey?: ISurvey | null;
}

export const defaultValue: Readonly<ISurveyQuestion> = {
  isNeed: false,
};
