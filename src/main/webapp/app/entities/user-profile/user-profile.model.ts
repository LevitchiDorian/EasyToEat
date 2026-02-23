import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { ILocation } from 'app/entities/location/location.model';
import { UserRole } from 'app/entities/enumerations/user-role.model';

export interface IUserProfile {
  id: number;
  phone?: string | null;
  avatarUrl?: string | null;
  role?: keyof typeof UserRole | null;
  preferredLanguage?: string | null;
  receiveEmailNotifications?: boolean | null;
  receivePushNotifications?: boolean | null;
  loyaltyPoints?: number | null;
  createdAt?: dayjs.Dayjs | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
  location?: Pick<ILocation, 'id' | 'name'> | null;
}

export type NewUserProfile = Omit<IUserProfile, 'id'> & { id: null };
