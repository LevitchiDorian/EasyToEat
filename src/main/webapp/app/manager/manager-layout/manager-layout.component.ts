import { Component, inject, signal, computed } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AccountService } from 'app/core/auth/account.service';
import { AccountRoleService } from 'app/core/profile/account-role.service';

@Component({
  selector: 'app-manager-layout',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './manager-layout.component.html',
  styleUrl: './manager-layout.component.scss',
})
export default class ManagerLayoutComponent {
  private readonly accountService = inject(AccountService);
  readonly accountRoleService = inject(AccountRoleService);

  sidebarOpen = signal(false);

  account = this.accountService.trackCurrentAccount();
  locationName = computed(() =>
    this.accountRoleService.profile()?.locationId ? `Locația ${this.accountRoleService.profile()!.locationId}` : 'Locația mea',
  );
  roleLabel = computed(() => {
    const r = this.accountRoleService.profile()?.role;
    if (r === 'MANAGER') return 'Manager';
    if (r === 'SUPER_ADMIN') return 'Super Admin';
    return 'Staff';
  });

  toggleSidebar(): void {
    this.sidebarOpen.update(v => !v);
  }

  closeSidebar(): void {
    this.sidebarOpen.set(false);
  }
}
