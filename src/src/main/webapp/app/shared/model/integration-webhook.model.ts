export interface IIntegrationWebhook {
  id?: number;
  name?: string;
  targetUrl?: string;
  secret?: string | null;
  isActive?: boolean;
  subscribedEvents?: string | null;
}

export const defaultValue: Readonly<IIntegrationWebhook> = {
  isActive: false,
};
