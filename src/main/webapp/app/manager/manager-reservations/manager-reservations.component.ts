import { Component, inject, signal, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { AccountRoleService } from 'app/core/profile/account-role.service';

interface ReservationItem {
  id: number;
  reservationCode: string;
  reservationDate: string;
  startTime: string;
  endTime: string;
  partySize: number;
  status: string;
  specialRequests?: string;
  location?: { id: number; name?: string };
  client?: { id: number; login?: string; firstName?: string; lastName?: string };
}

@Component({
  selector: 'app-manager-reservations',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="rv-admin-page">
      <div class="rv-admin-page-header">
        <div>
          <h1 class="rv-admin-page-title">Rezervări</h1>
          <p class="rv-admin-page-subtitle">Rezervările locației tale</p>
        </div>
        <div style="display:flex;gap:10px;align-items:center">
          <input type="date" class="mgr-date-input" [value]="selectedDate()" (change)="onDateChange($any($event.target).value)" />
          <button class="mgr-btn-refresh" (click)="load()" [disabled]="isLoading()">Reîncarcă</button>
        </div>
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
                <th>Cod</th>
                <th>Data</th>
                <th>Oră</th>
                <th>Persoane</th>
                <th>Client</th>
                <th>Stare</th>
                <th>Solicitări</th>
              </tr>
            </thead>
            <tbody>
              @for (r of reservations(); track r.id) {
                <tr>
                  <td class="mgr-mono">{{ r.reservationCode }}</td>
                  <td>{{ r.reservationDate }}</td>
                  <td>{{ r.startTime }} – {{ r.endTime }}</td>
                  <td>{{ r.partySize }}</td>
                  <td>{{ r.client?.firstName }} {{ r.client?.lastName }}</td>
                  <td>
                    <span class="rv-status-badge" [class]="badgeClass(r.status)">{{ statusLabel(r.status) }}</span>
                  </td>
                  <td style="max-width:200px;font-size:.78rem;color:rgba(255,255,255,.5)">{{ r.specialRequests || '—' }}</td>
                </tr>
              }
              @if (reservations().length === 0) {
                <tr>
                  <td colspan="7" style="text-align:center;color:rgba(255,255,255,.3);padding:32px">
                    Nicio rezervare pentru această dată.
                  </td>
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
      .mgr-date-input,
      .mgr-btn-refresh {
        background: rgba(255, 255, 255, 0.06);
        border: 1px solid rgba(255, 255, 255, 0.12);
        border-radius: 8px;
        color: #fff;
        padding: 6px 12px;
        font-size: 0.82rem;
        cursor: pointer;
        outline: none;
      }
      .mgr-btn-refresh {
        background: rgba(245, 165, 32, 0.1);
        border-color: rgba(245, 165, 32, 0.25);
        color: var(--rv-orange, #f5a520);
        &:hover {
          background: rgba(245, 165, 32, 0.2);
        }
        &:disabled {
          opacity: 0.5;
          cursor: not-allowed;
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
export default class ManagerReservationsComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly configService = inject(ApplicationConfigService);
  private readonly roleService = inject(AccountRoleService);

  reservations = signal<ReservationItem[]>([]);
  isLoading = signal(false);
  error = signal<string | null>(null);
  selectedDate = signal<string>(new Date().toISOString().substring(0, 10));

  ngOnInit(): void {
    // Ensure profile is loaded
    this.roleService.load();
    this.load();
  }

  load(): void {
    this.isLoading.set(true);
    this.error.set(null);
    const locationId = this.roleService.locationId();
    const date = this.selectedDate();
    let url = `api/reservations?reservationDate.equals=${date}&size=200&sort=startTime,asc`;
    if (locationId) url += `&locationId.equals=${locationId}`;
    this.http.get<ReservationItem[]>(this.configService.getEndpointFor(url)).subscribe({
      next: data => {
        this.reservations.set(data);
        this.isLoading.set(false);
      },
      error: () => {
        this.error.set('Eroare la încărcare.');
        this.isLoading.set(false);
      },
    });
  }

  onDateChange(d: string): void {
    this.selectedDate.set(d);
    this.load();
  }

  statusLabel(s: string): string {
    const map: Record<string, string> = {
      PENDING: 'În așteptare',
      CONFIRMED: 'Confirmată',
      COMPLETED: 'Finalizată',
      CANCELLED: 'Anulată',
      NO_SHOW: 'Absent',
    };
    return map[s] ?? s;
  }

  badgeClass(s: string): string {
    if (s === 'CONFIRMED') return 'rv-status-badge--blue';
    if (s === 'COMPLETED') return 'rv-status-badge--success';
    if (s === 'CANCELLED' || s === 'NO_SHOW') return 'rv-status-badge--danger';
    return 'rv-status-badge--warning';
  }
}
