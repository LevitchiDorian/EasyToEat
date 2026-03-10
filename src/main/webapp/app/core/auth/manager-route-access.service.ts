import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map, filter, take, switchMap } from 'rxjs/operators';
import { of, interval } from 'rxjs';

import { AccountService } from 'app/core/auth/account.service';
import { AccountRoleService } from 'app/core/profile/account-role.service';
import { StateStorageService } from './state-storage.service';

export const ManagerRouteAccessService: CanActivateFn = (_next, state) => {
  const accountService = inject(AccountService);
  const accountRoleService = inject(AccountRoleService);
  const router = inject(Router);
  const stateStorageService = inject(StateStorageService);

  const checkAllowed = (): boolean => {
    const allowed = accountRoleService.isManager() || accountService.hasAnyAuthority(['ROLE_ADMIN', 'ROLE_MANAGER']);
    if (!allowed) router.navigate(['accessdenied']);
    return allowed;
  };

  return accountService.identity().pipe(
    switchMap(account => {
      if (!account) {
        stateStorageService.storeUrl(state.url);
        router.navigate(['/login']);
        return of(false);
      }

      // Super admins always have access
      if (accountService.hasAnyAuthority(['ROLE_ADMIN'])) {
        return of(true);
      }

      // Trigger profile load if not done yet
      accountRoleService.load();

      if (accountRoleService.loaded()) {
        return of(checkAllowed());
      }

      // Wait for the profile to load
      return interval(50).pipe(
        filter(() => accountRoleService.loaded()),
        take(1),
        map(() => checkAllowed()),
      );
    }),
  );
};
