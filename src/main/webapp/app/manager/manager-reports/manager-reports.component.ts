import { Component, inject, signal, computed, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { AccountRoleService } from 'app/core/profile/account-role.service';
import { AccountService } from 'app/core/auth/account.service';

interface Reservation {
  id: number;
  reservationCode: string;
  reservationDate: string;
  startTime: string;
  endTime: string;
  partySize: number;
  status: string;
  client?: { id: number; firstName?: string; lastName?: string };
}

interface Order {
  id: number;
  status: string;
  totalAmount?: number;
  orderType?: string;
  createdAt?: string;
  client?: { id: number; firstName?: string; lastName?: string };
}

@Component({
  selector: 'app-manager-reports',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="rv-admin-page">
      <div class="rv-admin-page-header">
        <div>
          <h1 class="rv-admin-page-title">Rapoarte</h1>
          <p class="rv-admin-page-subtitle">Activitatea locației tale</p>
        </div>
        <div style="display:flex;gap:10px;align-items:center">
          <input type="date" class="rp-date-input" [value]="selectedDate()" (change)="onDateChange($any($event.target).value)" />
          <button class="rp-btn-refresh" (click)="load()" [disabled]="isLoading()">Reîncarcă</button>
        </div>
      </div>

      @if (isLoading()) {
        <div class="rp-loading">Se încarcă rapoartele...</div>
      } @else {
        <!-- Stats cards -->
        <div class="rp-stats">
          <div class="rp-stat-card rp-stat-card--orange">
            <div class="rp-stat-value">{{ reservations().length }}</div>
            <div class="rp-stat-label">Rezervări azi</div>
          </div>
          <div class="rp-stat-card rp-stat-card--blue">
            <div class="rp-stat-value">{{ confirmedReservations() }}</div>
            <div class="rp-stat-label">Rezervări confirmate</div>
          </div>
          <div class="rp-stat-card rp-stat-card--green">
            <div class="rp-stat-value">{{ orders().length }}</div>
            <div class="rp-stat-label">Comenzi azi</div>
          </div>
          <div class="rp-stat-card rp-stat-card--green">
            <div class="rp-stat-value">{{ totalRevenue() | number: '1.0-0' }} MDL</div>
            <div class="rp-stat-label">Venit azi</div>
          </div>
          <div class="rp-stat-card">
            <div class="rp-stat-value">{{ totalGuests() }}</div>
            <div class="rp-stat-label">Invitați totali</div>
          </div>
          <div class="rp-stat-card">
            <div class="rp-stat-value">{{ avgPartySize() | number: '1.1-1' }}</div>
            <div class="rp-stat-label">Medie grup</div>
          </div>
        </div>

        <!-- Two-column tables -->
        <div class="rp-two-col">
          <!-- Reservations table -->
          <div class="glass rp-panel">
            <h3 class="rp-panel-title">Rezervări – {{ selectedDate() }}</h3>
            @if (reservations().length === 0) {
              <p class="rp-empty">Nicio rezervare pentru această dată.</p>
            } @else {
              <div class="rv-admin-table-wrap">
                <table class="rv-admin-table">
                  <thead>
                    <tr>
                      <th>Oră</th>
                      <th>Client</th>
                      <th>Pers.</th>
                      <th>Stare</th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (r of reservations(); track r.id) {
                      <tr>
                        <td class="rp-mono">{{ r.startTime }}</td>
                        <td>{{ r.client?.firstName }} {{ r.client?.lastName }}</td>
                        <td>{{ r.partySize }}</td>
                        <td>
                          <span class="rv-status-badge" [class]="resBadge(r.status)">{{ resLabel(r.status) }}</span>
                        </td>
                      </tr>
                    }
                  </tbody>
                </table>
              </div>
            }
          </div>

          <!-- Orders table -->
          <div class="glass rp-panel">
            <h3 class="rp-panel-title">Comenzi – {{ selectedDate() }}</h3>
            @if (orders().length === 0) {
              <p class="rp-empty">Nicio comandă pentru această dată.</p>
            } @else {
              <div class="rv-admin-table-wrap">
                <table class="rv-admin-table">
                  <thead>
                    <tr>
                      <th>#</th>
                      <th>Client</th>
                      <th>Tip</th>
                      <th>Total</th>
                      <th>Stare</th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (o of orders(); track o.id) {
                      <tr>
                        <td class="rp-mono">#{{ o.id }}</td>
                        <td>{{ o.client?.firstName }} {{ o.client?.lastName }}</td>
                        <td style="font-size:.75rem;color:rgba(255,255,255,.45)">{{ orderType(o) }}</td>
                        <td>{{ o.totalAmount ?? 0 | number: '1.0-0' }} MDL</td>
                        <td>
                          <span class="rv-status-badge" [class]="ordBadge(o.status)">{{ ordLabel(o.status) }}</span>
                        </td>
                      </tr>
                    }
                  </tbody>
                </table>
              </div>
            }
          </div>
        </div>

        <!-- Hourly distribution -->
        @if (hourlyData().length > 0) {
          <div class="glass rp-panel" style="margin-top:20px">
            <h3 class="rp-panel-title">Ore aglomerate – rezervări</h3>
            <div class="rp-hours">
              @for (h of hourlyData(); track h.hour) {
                <div class="rp-hour-bar">
                  <div class="rp-hour-fill" [style.height.%]="h.pct" [title]="h.count + ' rezervări la ' + h.label"></div>
                  <div class="rp-hour-label">{{ h.label }}</div>
                </div>
              }
            </div>
          </div>
        }
      }
    </div>
  `,
  styles: [
    `
      .rp-loading {
        padding: 40px;
        text-align: center;
        color: rgba(255, 255, 255, 0.4);
      }
      .rp-stats {
        display: flex;
        gap: 12px;
        flex-wrap: wrap;
        margin-bottom: 24px;
      }
      .rp-stat-card {
        flex: 1;
        min-width: 120px;
        background: rgba(255, 255, 255, 0.04);
        border: 1px solid rgba(255, 255, 255, 0.08);
        border-radius: 12px;
        padding: 16px;
        text-align: center;
      }
      .rp-stat-value {
        font-size: 1.6rem;
        font-weight: 700;
        color: rgba(255, 255, 255, 0.85);
      }
      .rp-stat-label {
        font-size: 0.7rem;
        color: rgba(255, 255, 255, 0.4);
        margin-top: 3px;
        text-transform: uppercase;
        letter-spacing: 0.04em;
      }
      .rp-stat-card--orange {
        border-color: rgba(245, 165, 32, 0.25);
        .rp-stat-value {
          color: #f5a520;
        }
      }
      .rp-stat-card--blue {
        border-color: rgba(59, 130, 246, 0.25);
        .rp-stat-value {
          color: #60a5fa;
        }
      }
      .rp-stat-card--green {
        border-color: rgba(34, 197, 94, 0.25);
        .rp-stat-value {
          color: #22c55e;
        }
      }
      .rp-two-col {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 20px;
      }
      .rp-panel {
        padding: 16px;
        border-radius: 12px;
      }
      .rp-panel-title {
        font-size: 0.85rem;
        font-weight: 700;
        color: rgba(255, 255, 255, 0.7);
        text-transform: uppercase;
        letter-spacing: 0.04em;
        margin-bottom: 14px;
      }
      .rp-empty {
        font-size: 0.82rem;
        color: rgba(255, 255, 255, 0.3);
        padding: 12px 0;
      }
      .rp-mono {
        font-family: monospace;
        color: var(--rv-orange, #f5a520);
        font-size: 0.78rem;
      }
      .rp-date-input,
      .rp-btn-refresh {
        background: rgba(255, 255, 255, 0.06);
        border: 1px solid rgba(255, 255, 255, 0.12);
        border-radius: 8px;
        color: #fff;
        padding: 6px 12px;
        font-size: 0.82rem;
        cursor: pointer;
        outline: none;
      }
      .rp-btn-refresh {
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
      /* Hourly chart */
      .rp-hours {
        display: flex;
        align-items: flex-end;
        gap: 6px;
        height: 80px;
        padding-bottom: 20px;
        position: relative;
      }
      .rp-hour-bar {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: flex-end;
        height: 100%;
        position: relative;
      }
      .rp-hour-fill {
        width: 100%;
        min-height: 4px;
        background: rgba(245, 165, 32, 0.6);
        border-radius: 3px 3px 0 0;
        transition: height 0.3s;
      }
      .rp-hour-label {
        font-size: 0.6rem;
        color: rgba(255, 255, 255, 0.35);
        position: absolute;
        bottom: -16px;
        white-space: nowrap;
      }
      @media (max-width: 900px) {
        .rp-two-col {
          grid-template-columns: 1fr;
        }
      }
    `,
  ],
})
export default class ManagerReportsComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly configService = inject(ApplicationConfigService);
  private readonly roleService = inject(AccountRoleService);
  private readonly accountService = inject(AccountService);

  isLoading = signal(true);
  selectedDate = signal<string>(new Date().toISOString().substring(0, 10));
  reservations = signal<Reservation[]>([]);
  orders = signal<Order[]>([]);

  confirmedReservations = computed(() => this.reservations().filter(r => r.status === 'CONFIRMED' || r.status === 'COMPLETED').length);
  totalGuests = computed(() =>
    this.reservations()
      .filter(r => r.status !== 'CANCELLED')
      .reduce((s, r) => s + (r.partySize ?? 0), 0),
  );
  avgPartySize = computed(() => {
    const active = this.reservations().filter(r => r.status !== 'CANCELLED');
    return active.length > 0 ? active.reduce((s, r) => s + (r.partySize ?? 0), 0) / active.length : 0;
  });
  totalRevenue = computed(() =>
    this.orders()
      .filter(o => o.status !== 'CANCELLED')
      .reduce((s, o) => s + (o.totalAmount ?? 0), 0),
  );

  hourlyData = computed(() => {
    const counts: Record<number, number> = {};
    for (let h = 9; h <= 22; h++) counts[h] = 0;
    for (const r of this.reservations()) {
      if (r.status === 'CANCELLED') continue;
      const h = parseInt(r.startTime?.substring(0, 2) ?? '0');
      if (h >= 9 && h <= 22) counts[h] = (counts[h] ?? 0) + 1;
    }
    const max = Math.max(...Object.values(counts), 1);
    return Object.entries(counts).map(([h, count]) => ({
      hour: +h,
      label: `${h}:00`,
      count,
      pct: Math.round((count / max) * 100),
    }));
  });

  ngOnInit(): void {
    this.roleService.load();
    setTimeout(() => this.load(), 300);
  }

  load(): void {
    this.isLoading.set(true);
    const locationId = this.roleService.locationId();
    const isAdmin = this.accountService.hasAnyAuthority(['ROLE_ADMIN']);
    const date = this.selectedDate();

    let resUrl = `api/reservations?reservationDate.equals=${date}&size=500&sort=startTime,asc`;
    let ordUrl = `api/restaurant-orders?size=200&sort=id,desc`;

    if (locationId) {
      resUrl += `&locationId.equals=${locationId}`;
      ordUrl += `&locationId.equals=${locationId}`;
    }

    forkJoin({
      reservations: this.http.get<Reservation[]>(this.configService.getEndpointFor(resUrl)),
      orders: this.http.get<Order[]>(this.configService.getEndpointFor(ordUrl)),
    }).subscribe({
      next: ({ reservations, orders }) => {
        this.reservations.set(reservations);
        // Filter orders to the selected date
        const dateStr = date;
        this.orders.set(
          orders.filter(o => {
            if (!o.createdAt) return true;
            return o.createdAt.startsWith(dateStr);
          }),
        );
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false),
    });
  }

  onDateChange(d: string): void {
    this.selectedDate.set(d);
    this.load();
  }

  resLabel(s: string): string {
    const m: Record<string, string> = {
      PENDING: 'Așteptare',
      CONFIRMED: 'Confirmată',
      COMPLETED: 'Finalizată',
      CANCELLED: 'Anulată',
      NO_SHOW: 'Absent',
    };
    return m[s] ?? s;
  }
  resBadge(s: string): string {
    if (s === 'CONFIRMED') return 'rv-status-badge--blue';
    if (s === 'COMPLETED') return 'rv-status-badge--success';
    if (s === 'CANCELLED' || s === 'NO_SHOW') return 'rv-status-badge--danger';
    return 'rv-status-badge--warning';
  }
  ordLabel(s: string): string {
    const m: Record<string, string> = {
      PENDING: 'Așteptare',
      CONFIRMED: 'Confirmată',
      PREPARING: 'Preparare',
      READY: 'Gata',
      DELIVERED: 'Livrată',
      COMPLETED: 'Finalizată',
      CANCELLED: 'Anulată',
    };
    return m[s] ?? s;
  }
  ordBadge(s: string): string {
    if (s === 'COMPLETED' || s === 'DELIVERED') return 'rv-status-badge--success';
    if (s === 'CANCELLED') return 'rv-status-badge--danger';
    if (s === 'PREPARING' || s === 'READY') return 'rv-status-badge--blue';
    return 'rv-status-badge--warning';
  }
  orderType(o: Order): string {
    return o.orderType === 'DELIVERY' ? 'Livrare' : o.orderType === 'TAKEAWAY' ? 'Pachet' : 'La masă';
  }
}
