import { Component, inject, computed } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AccountService } from 'app/core/auth/account.service';
import { AccountRoleService } from 'app/core/profile/account-role.service';
import { LoginService } from 'app/login/login.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-chef-layout',
  standalone: true,
  templateUrl: './chef-layout.component.html',
  styleUrl: './chef-layout.component.scss',
  imports: [RouterModule],
})
export default class ChefLayoutComponent {
  private readonly accountService = inject(AccountService);
  private readonly accountRoleService = inject(AccountRoleService);
  private readonly loginService = inject(LoginService);
  private readonly router = inject(Router);

  account = this.accountService.trackCurrentAccount();

  locationLabel = computed(() => {
    const locId = this.accountRoleService.locationId();
    return locId ? `Locația ${locId}` : 'Bucătărie';
  });

  logout(): void {
    this.loginService.logout();
    this.accountRoleService.reset();
    this.router.navigate(['']);
  }
}
