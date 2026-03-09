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
                <th>Oră</th>
                <th>Persoane</th>
                <th>Client</th>
                <th>Solicitări</th>
                <th>Stare</th>
                <th>Acțiuni</th>
              </tr>
            </thead>
            <tbody>
              @for (r of reservations(); track r.id) {
                <tr>
                  <td class="mgr-mono">{{ r.reservationCode }}</td>
                  <td>{{ r.startTime }} – {{ r.endTime }}</td>
                  <td>{{ r.partySize }}</td>
                  <td>{{ r.client?.firstName }} {{ r.client?.lastName }}</td>
                  <td style="max-width:180px;font-size:.78rem;color:rgba(255,255,255,.5)">{{ r.specialRequests || '—' }}</td>
                  <td>
                    <span class="rv-status-badge" [class]="badgeClass(r.status)">{{ statusLabel(r.status) }}</span>
                  </td>
                  <td>
                    <div class="mr-actions">
                      @if (r.status === 'PENDING') {
                        <button class="mr-action-btn mr-action-btn--confirm" (click)="updateStatus(r, 'CONFIRMED')">✓ Confirmă</button>
                      }
                      @if (r.status === 'CONFIRMED') {
                        <button class="mr-action-btn mr-action-btn--complete" (click)="updateStatus(r, 'COMPLETED')">✓ Finalizat</button>
                        <button class="mr-action-btn mr-action-btn--noshow" (click)="updateStatus(r, 'NO_SHOW')">Absent</button>
                      }
                      @if (r.status !== 'CANCELLED' && r.status !== 'COMPLETED' && r.status !== 'NO_SHOW') {
                        <button class="mr-action-btn mr-action-btn--cancel" (click)="updateStatus(r, 'CANCELLED')">✕ Anulează</button>
                      }
                    </div>
                  </td>
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
      .mr-actions {
        display: flex;
        gap: 5px;
        flex-wrap: wrap;
      }
      .mr-action-btn {
        padding: 3px 8px;
        border-radius: 5px;
        font-size: 0.7rem;
        font-weight: 600;
        border: 1px solid;
        cursor: pointer;
        transition: all 0.15s;
        white-space: nowrap;
        &--confirm {
          background: rgba(34, 197, 94, 0.1);
          border-color: rgba(34, 197, 94, 0.3);
          color: #22c55e;
          &:hover {
            background: rgba(34, 197, 94, 0.2);
          }
        }
        &--complete {
          background: rgba(59, 130, 246, 0.1);
          border-color: rgba(59, 130, 246, 0.3);
          color: #60a5fa;
          &:hover {
            background: rgba(59, 130, 246, 0.2);
          }
        }
        &--noshow {
          background: rgba(245, 158, 11, 0.1);
          border-color: rgba(245, 158, 11, 0.3);
          color: #f59e0b;
          &:hover {
            background: rgba(245, 158, 11, 0.2);
          }
        }
        &--cancel {
          background: rgba(239, 68, 68, 0.1);
          border-color: rgba(239, 68, 68, 0.3);
          color: #ef4444;
          &:hover {
            background: rgba(239, 68, 68, 0.2);
          }
        }
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

  updateStatus(res: ReservationItem, newStatus: string): void {
    if (newStatus === 'CANCELLED' && !confirm(`Anulezi rezervarea ${res.reservationCode}?`)) return;
    this.http
      .patch<ReservationItem>(this.configService.getEndpointFor(`api/reservations/${res.id}`), { id: res.id, status: newStatus })
      .subscribe({
        next: () => {
          this.reservations.update(list => list.map(r => (r.id === res.id ? { ...r, status: newStatus } : r)));
        },
        error: () => alert('Eroare la actualizare. Încearcă din nou.'),
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
