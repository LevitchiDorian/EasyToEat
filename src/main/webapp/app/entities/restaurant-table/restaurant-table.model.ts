import { IDiningRoom } from 'app/entities/dining-room/dining-room.model';
import { TableShape } from 'app/entities/enumerations/table-shape.model';
import { TableStatus } from 'app/entities/enumerations/table-status.model';

export interface IRestaurantTable {
  id: number;
  tableNumber?: string | null;
  shape?: keyof typeof TableShape | null;
  minCapacity?: number | null;
  maxCapacity?: number | null;
  positionX?: number | null;
  positionY?: number | null;
  widthPx?: number | null;
  heightPx?: number | null;
  rotation?: number | null;
  status?: keyof typeof TableStatus | null;
  isActive?: boolean | null;
  notes?: string | null;
  room?: Pick<IDiningRoom, 'id' | 'name'> | null;
}

export type NewRestaurantTable = Omit<IRestaurantTable, 'id'> & { id: null };
