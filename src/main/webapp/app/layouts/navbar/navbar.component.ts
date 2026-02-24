import { Component, OnInit, HostListener, inject, signal, computed } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { CommonModule } from '@angular/common';

import { StateStorageService } from 'app/core/auth/state-storage.service';
import { AccountService } from 'app/core/auth/account.service';
import { LoginService } from 'app/login/login.service';
import { ProfileService } from 'app/layouts/profiles/profile.service';
import { Authority } from 'app/config/authority.constants';

@Component({
  selector: 'jhi-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
  imports: [RouterModule, CommonModule, NgbDropdownModule],
})
export default class NavbarComponent implements OnInit {
  isNavbarCollapsed = signal(true);
  scrolled = false;
  account = inject(AccountService).trackCurrentAccount();
  isAdmin = computed(() => this.accountService.hasAnyAuthority([Authority.ADMIN]));

  private readonly accountService = inject(AccountService);
  private readonly loginService = inject(LoginService);
  private readonly stateStorageService = inject(StateStorageService);
  private readonly profileService = inject(ProfileService);
  private readonly router = inject(Router);
  private readonly translateService = inject(TranslateService);

  ngOnInit(): void {
    this.profileService.getProfileInfo().subscribe();
  }

  @HostListener('window:scroll')
  onScroll(): void {
    this.scrolled = window.scrollY > 10;
  }

  collapseNavbar(): void {
    this.isNavbarCollapsed.set(true);
  }

  toggleNavbar(): void {
    this.isNavbarCollapsed.update(v => !v);
  }

  login(): void {
    this.stateStorageService.storeUrl(this.router.routerState.snapshot.url);
    this.router.navigate(['/login']);
  }

  logout(): void {
    this.collapseNavbar();
    this.loginService.logout();
    this.router.navigate(['']);
  }
}
