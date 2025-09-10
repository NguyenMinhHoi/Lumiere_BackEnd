import { ITicket } from 'app/shared/model/ticket.model';
import { IKnowledgeArticle } from 'app/shared/model/knowledge-article.model';

export interface ITag {
  id?: number;
  name?: string;
  tickets?: ITicket[] | null;
  articles?: IKnowledgeArticle[] | null;
}

export const defaultValue: Readonly<ITag> = {};
