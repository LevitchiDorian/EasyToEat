import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IRestaurantOrder } from 'app/entities/restaurant-order/restaurant-order.model';
import { PaymentMethod } from 'app/entities/enumerations/payment-method.model';
import { PaymentStatus } from 'app/entities/enumerations/payment-status.model';

export interface IPayment {
  id: number;
  transactionCode?: string | null;
  amount?: number | null;
  method?: keyof typeof PaymentMethod | null;
  status?: keyof typeof PaymentStatus | null;
  paidAt?: dayjs.Dayjs | null;
  receiptUrl?: string | null;
  notes?: string | null;
  createdAt?: dayjs.Dayjs | null;
  processedBy?: Pick<IUser, 'id' | 'login'> | null;
  order?: Pick<IRestaurantOrder, 'id' | 'orderCode'> | null;
}

export type NewPayment = Omit<IPayment, 'id'> & { id: null };
