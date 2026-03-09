import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface UserProfileInfo {
  role: 'SUPER_ADMIN' | 'MANAGER' | 'STAFF' | 'CLIENT' | null;
  locationId: number | null;
}

@Injectable({ providedIn: 'root' })
export class AccountRoleService {
  private readonly http = inject(HttpClient);

  profile = signal<UserProfileInfo | null>(null);
  loaded = signal(false);

  load(): void {
    if (this.loaded()) return;
    this.http.get<UserProfileInfo>('/api/account/profile').subscribe({
      next: data => {
        this.profile.set(data);
        this.loaded.set(true);
      },
      error: () => {
        this.profile.set(null);
        this.loaded.set(true);
      },
    });
  }

  reset(): void {
    this.profile.set(null);
    this.loaded.set(false);
  }

  isManager(): boolean {
    const r = this.profile()?.role;
    return r === 'MANAGER' || r === 'SUPER_ADMIN';
  }

  isStaff(): boolean {
    return this.profile()?.role === 'STAFF';
  }

  hasLocationAccess(): boolean {
    return this.isManager() || this.isStaff();
  }

  locationId(): number | null {
    return this.profile()?.locationId ?? null;
  }
}
