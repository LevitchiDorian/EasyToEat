import dayjs from 'dayjs/esm';
import { ILocation } from 'app/entities/location/location.model';
import { IUser } from 'app/entities/user/user.model';
import { IDiningRoom } from 'app/entities/dining-room/dining-room.model';

export interface IWaitingList {
  id: number;
  requestedDate?: dayjs.Dayjs | null;
  requestedTime?: string | null;
  partySize?: number | null;
  notes?: string | null;
  isNotified?: boolean | null;
  expiresAt?: dayjs.Dayjs | null;
  createdAt?: dayjs.Dayjs | null;
  location?: Pick<ILocation, 'id' | 'name'> | null;
  client?: Pick<IUser, 'id' | 'login'> | null;
  room?: Pick<IDiningRoom, 'id' | 'name'> | null;
}

export type NewWaitingList = Omit<IWaitingList, 'id'> & { id: null };
