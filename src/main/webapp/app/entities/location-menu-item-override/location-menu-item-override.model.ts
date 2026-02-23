import { IMenuItem } from 'app/entities/menu-item/menu-item.model';
import { ILocation } from 'app/entities/location/location.model';

export interface ILocationMenuItemOverride {
  id: number;
  isAvailableAtLocation?: boolean | null;
  priceOverride?: number | null;
  preparationTimeOverride?: number | null;
  notes?: string | null;
  menuItem?: Pick<IMenuItem, 'id' | 'name'> | null;
  location?: Pick<ILocation, 'id' | 'name'> | null;
}

export type NewLocationMenuItemOverride = Omit<ILocationMenuItemOverride, 'id'> & { id: null };
