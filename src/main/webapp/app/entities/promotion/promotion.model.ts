import dayjs from 'dayjs/esm';
import { IBrand } from 'app/entities/brand/brand.model';
import { ILocation } from 'app/entities/location/location.model';
import { DiscountType } from 'app/entities/enumerations/discount-type.model';

export interface IPromotion {
  id: number;
  code?: string | null;
  name?: string | null;
  description?: string | null;
  discountType?: keyof typeof DiscountType | null;
  discountValue?: number | null;
  minimumOrderAmount?: number | null;
  maxUsageCount?: number | null;
  currentUsageCount?: number | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  isActive?: boolean | null;
  brand?: Pick<IBrand, 'id' | 'name'> | null;
  location?: Pick<ILocation, 'id' | 'name'> | null;
}

export type NewPromotion = Omit<IPromotion, 'id'> & { id: null };
