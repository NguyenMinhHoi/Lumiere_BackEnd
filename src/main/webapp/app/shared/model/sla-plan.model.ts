export interface ISlaPlan {
  id?: number;
  name?: string;
  firstResponseMins?: number;
  resolutionMins?: number;
  active?: boolean;
}

export const defaultValue: Readonly<ISlaPlan> = {
  active: false,
};
