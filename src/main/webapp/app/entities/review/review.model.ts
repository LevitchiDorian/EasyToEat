import dayjs from 'dayjs/esm';
import { ILocation } from 'app/entities/location/location.model';
import { IReservation } from 'app/entities/reservation/reservation.model';
import { IUser } from 'app/entities/user/user.model';

export interface IReview {
  id: number;
  overallRating?: number | null;
  foodRating?: number | null;
  serviceRating?: number | null;
  ambienceRating?: number | null;
  comment?: string | null;
  isApproved?: boolean | null;
  isAnonymous?: boolean | null;
  createdAt?: dayjs.Dayjs | null;
  location?: Pick<ILocation, 'id' | 'name'> | null;
  reservation?: Pick<IReservation, 'id' | 'reservationCode'> | null;
  client?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewReview = Omit<IReview, 'id'> & { id: null };
