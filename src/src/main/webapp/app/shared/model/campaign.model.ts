import dayjs from 'dayjs';
import { DeliveryChannel } from 'app/shared/model/enumerations/delivery-channel.model';

export interface ICampaign {
  id?: number;
  name?: string;
  description?: string | null;
  channel?: keyof typeof DeliveryChannel;
  budget?: number | null;
  startDate?: dayjs.Dayjs;
  endDate?: dayjs.Dayjs;
  isActive?: boolean;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<ICampaign> = {
  isActive: false,
};
