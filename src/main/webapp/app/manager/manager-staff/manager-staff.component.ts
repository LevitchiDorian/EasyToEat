import { Component, inject, signal, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { AccountRoleService } from 'app/core/profile/account-role.service';

interface StaffMember {
  id: number;
  phone?: string;
  role: string;
  user?: { id: number; login?: string; firstName?: string; lastName?: string; email?: string };
}

@Component({
  selector: 'app-manager-staff',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule],
  template: `
    <div class="rv-admin-page">
      <div class="rv-admin-page-header">
        <div>
          <h1 class="rv-admin-page-title">Personal</h1>
          <p class="rv-admin-page-subtitle">Echipa locației tale</p>
        </div>
        <button class="mgr-btn-refresh" (click)="load()">Reîncarcă</button>
      </div>

      @if (isLoading()) {
        <div style="padding:40px;text-align:center;color:rgba(255,255,255,.4)">Se încarcă...</div>
      } @else if (error()) {
        <div style="padding:20px;color:#ef4444">{{ error() }}</div>
      } @else {
        <div class="rv-admin-table-wrap">
          <table class="rv-admin-table">
            <thead>
              <tr>
                <th>Utilizator</th>
                <th>Nume</th>
                <th>Email</th>
                <th>Telefon</th>
                <th>Rol</th>
              </tr>
            </thead>
            <tbody>
              @for (s of staff(); track s.id) {
                <tr>
                  <td class="mgr-mono">{{ s.user?.login }}</td>
                  <td>{{ s.user?.firstName }} {{ s.user?.lastName }}</td>
                  <td style="font-size:.8rem;color:rgba(255,255,255,.5)">{{ s.user?.email }}</td>
                  <td>{{ s.phone || '—' }}</td>
                  <td>
                    <span class="rv-status-badge" [class]="roleClass(s.role)">
                      {{ roleLabel(s.role) }}
                    </span>
                  </td>
                </tr>
              }
              @if (staff().length === 0) {
                <tr>
                  <td colspan="5" style="text-align:center;color:rgba(255,255,255,.3);padding:32px">Niciun membru de personal găsit.</td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      }
    </div>
  `,
  styles: [
    `
      .mgr-btn-refresh {
        background: rgba(245, 165, 32, 0.1);
        border: 1px solid rgba(245, 165, 32, 0.25);
        border-radius: 8px;
        color: var(--rv-orange, #f5a520);
        padding: 6px 14px;
        font-size: 0.82rem;
        cursor: pointer;
        &:hover {
          background: rgba(245, 165, 32, 0.2);
        }
      }
      .mgr-mono {
        font-family: monospace;
        font-size: 0.78rem;
        color: var(--rv-orange, #f5a520);
      }
    `,
  ],
})
export default class ManagerStaffComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly configService = inject(ApplicationConfigService);
  private readonly roleService = inject(AccountRoleService);

  staff = signal<StaffMember[]>([]);
  isLoading = signal(false);
  error = signal<string | null>(null);

  ngOnInit(): void {
    this.roleService.load();
    this.load();
  }

  load(): void {
    this.isLoading.set(true);
    this.error.set(null);
    const locationId = this.roleService.locationId();
    let url = `api/user-profiles?size=200&sort=id,asc`;
    if (locationId) url += `&locationId.equals=${locationId}`;
    this.http.get<StaffMember[]>(this.configService.getEndpointFor(url)).subscribe({
      next: data => {
        // Filter to only MANAGER and STAFF profiles
        this.staff.set(data.filter(s => s.role === 'MANAGER' || s.role === 'STAFF'));
        this.isLoading.set(false);
      },
      error: () => {
        this.error.set('Eroare la încărcare.');
        this.isLoading.set(false);
      },
    });
  }

  roleLabel(r: string): string {
    const map: Record<string, string> = { MANAGER: 'Manager', STAFF: 'Staff', CLIENT: 'Client', SUPER_ADMIN: 'Super Admin' };
    return map[r] ?? r;
  }

  roleClass(r: string): string {
    if (r === 'MANAGER' || r === 'SUPER_ADMIN') return 'rv-status-badge--warning';
    if (r === 'STAFF') return 'rv-status-badge--blue';
    return '';
  }
}
