import dayjs from 'dayjs/esm';
import { ILocation } from 'app/entities/location/location.model';
import { IUser } from 'app/entities/user/user.model';
import { IRestaurantTable } from 'app/entities/restaurant-table/restaurant-table.model';
import { IPromotion } from 'app/entities/promotion/promotion.model';
import { IReservation } from 'app/entities/reservation/reservation.model';
import { OrderStatus } from 'app/entities/enumerations/order-status.model';

export interface IRestaurantOrder {
  id: number;
  orderCode?: string | null;
  status?: keyof typeof OrderStatus | null;
  isPreOrder?: boolean | null;
  scheduledFor?: dayjs.Dayjs | null;
  subtotal?: number | null;
  discountAmount?: number | null;
  taxAmount?: number | null;
  totalAmount?: number | null;
  specialInstructions?: string | null;
  estimatedReadyTime?: dayjs.Dayjs | null;
  confirmedAt?: dayjs.Dayjs | null;
  completedAt?: dayjs.Dayjs | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  location?: Pick<ILocation, 'id' | 'name'> | null;
  client?: Pick<IUser, 'id' | 'login'> | null;
  assignedWaiter?: Pick<IUser, 'id' | 'login'> | null;
  table?: Pick<IRestaurantTable, 'id' | 'tableNumber'> | null;
  promotion?: Pick<IPromotion, 'id' | 'code'> | null;
  reservation?: Pick<IReservation, 'id'> | null;
}

export type NewRestaurantOrder = Omit<IRestaurantOrder, 'id'> & { id: null };
