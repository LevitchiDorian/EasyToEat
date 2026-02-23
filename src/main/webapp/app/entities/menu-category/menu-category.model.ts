import { IBrand } from 'app/entities/brand/brand.model';

export interface IMenuCategory {
  id: number;
  name?: string | null;
  description?: string | null;
  imageUrl?: string | null;
  displayOrder?: number | null;
  isActive?: boolean | null;
  parent?: Pick<IMenuCategory, 'id' | 'name'> | null;
  brand?: Pick<IBrand, 'id' | 'name'> | null;
}

export type NewMenuCategory = Omit<IMenuCategory, 'id'> & { id: null };
