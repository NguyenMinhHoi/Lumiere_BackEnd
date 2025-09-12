import dayjs from 'dayjs';

export interface IKnowledgeArticle {
  id?: number;
  categoryId?: number | null;
  title?: string;
  content?: string;
  published?: boolean;
  views?: number;
  updatedAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<IKnowledgeArticle> = {
  published: false,
};
