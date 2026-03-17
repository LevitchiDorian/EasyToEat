import { Injectable, inject } from '@angular/core';
import { Observable, shareReplay } from 'rxjs';
import { map } from 'rxjs/operators';
import { BrandService } from 'app/entities/brand/service/brand.service';
import { IBrand } from 'app/entities/brand/brand.model';

/**
 * Caches the brands list in memory for the lifetime of the app session.
 * Prevents repeated API calls when navigating between Home → Restaurants → Restaurant Detail.
 */
@Injectable({ providedIn: 'root' })
export class BrandCacheService {
  private readonly brandService = inject(BrandService);

  private brands$: Observable<IBrand[]> | null = null;

  getBrands(): Observable<IBrand[]> {
    if (!this.brands$) {
      this.brands$ = this.brandService.query({ size: 20, sort: ['id,asc'] }).pipe(
        map(res => res.body ?? []),
        shareReplay(1),
      );
    }
    return this.brands$;
  }

  /** Force a refresh on next call (e.g. after admin creates a new brand). */
  invalidate(): void {
    this.brands$ = null;
  }
}
