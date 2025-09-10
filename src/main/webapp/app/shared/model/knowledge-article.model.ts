import dayjs from 'dayjs';
import { IKnowledgeCategory } from 'app/shared/model/knowledge-category.model';
import { ITag } from 'app/shared/model/tag.model';

export interface IKnowledgeArticle {
  id?: number;
  title?: string;
  content?: string;
  published?: boolean;
  views?: number;
  updatedAt?: dayjs.Dayjs | null;
  category?: IKnowledgeCategory | null;
  tags?: ITag[] | null;
}

export const defaultValue: Readonly<IKnowledgeArticle> = {
  published: false,
};
