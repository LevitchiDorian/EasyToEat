import { ILocation } from 'app/entities/location/location.model';

export interface IDiningRoom {
  id: number;
  name?: string | null;
  description?: string | null;
  floor?: number | null;
  capacity?: number | null;
  isActive?: boolean | null;
  floorPlanUrl?: string | null;
  widthPx?: number | null;
  heightPx?: number | null;
  location?: Pick<ILocation, 'id' | 'name'> | null;
}

export type NewDiningRoom = Omit<IDiningRoom, 'id'> & { id: null };
