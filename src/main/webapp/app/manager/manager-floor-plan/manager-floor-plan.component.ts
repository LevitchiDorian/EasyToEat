import { Component, inject, signal, computed, OnInit, OnDestroy, ChangeDetectionStrategy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { interval, Subscription } from 'rxjs';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { AccountService } from 'app/core/auth/account.service';
import { FloorMapComponent, FloorPlan, FloorTable } from 'app/public/floor-map/floor-map.component';
import { StaffFloorPlanResponse, StaffTableInfo, LocationOption } from 'app/admin/staff-floor-plan/staff-floor-plan.component';

interface ReservationItem {
  id: number;
  reservationCode: string;
  reservationDate: string;
  startTime: string;
  endTime: string;
  partySize: number;
  status: string;
  specialRequests?: string;
  client?: { id: number; firstName?: string; lastName?: string };
}

interface OrderItem {
  id: number;
  orderNumber?: string;
  status: string;
  totalAmount?: number;
  createdAt?: string;
  notes?: string;
  client?: { id: number; firstName?: string; lastName?: string };
  table?: { id: number; tableNumber?: string };
}

type SideTab = 'masa' | 'rezervari' | 'comenzi';

@Component({
  selector: 'app-manager-floor-plan',
  standalone: true,
  templateUrl: './manager-floor-plan.component.html',
  styleUrl: './manager-floor-plan.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, FormsModule, FloorMapComponent],
})
export default class ManagerFloorPlanComponent implements OnInit, OnDestroy {
  private readonly http = inject(HttpClient);
  private readonly configService = inject(ApplicationConfigService);
  private readonly accountService = inject(AccountService);

  isAdmin = signal(false);
  isLoading = signal(false);
  error = signal<string | null>(null);
  locations = signal<LocationOption[]>([]);
  selectedLocationId = signal<number | null>(null);
  selectedDate = signal<string>(new Date().toISOString().substring(0, 10));
  rawResponse = signal<StaffFloorPlanResponse | null>(null);
  floorPlan = signal<FloorPlan | null>(null);
  selectedTable = signal<StaffTableInfo | null>(null);
  refreshCountdown = signal<number>(30);
  lastRefreshed = signal<string>('');

  activeTab = signal<SideTab>('rezervari');
  allReservations = signal<ReservationItem[]>([]);
  isLoadingReservations = signal(false);
  activeOrders = signal<OrderItem[]>([]);
  isLoadingOrders = signal(false);

  private refreshSub?: Subscription;

  totalTables = computed(() => {
    const r = this.rawResponse();
    if (!r) return 0;
    return r.rooms.reduce((acc, room) => acc + room.tables.filter(t => t.isActive).length, 0);
  });

  availableTables = computed(() => {
    const r = this.rawResponse();
    if (!r) return 0;
    return r.rooms.reduce((acc, room) => acc + room.tables.filter(t => t.status === 'AVAILABLE' && t.isActive).length, 0);
  });

  reservedTables = computed(() => {
    const r = this.rawResponse();
    if (!r) return 0;
    return r.rooms.reduce((acc, room) => acc + room.tables.filter(t => t.status === 'RESERVED' && t.isActive).length, 0);
  });

  occupiedTables = computed(() => {
    const r = this.rawResponse();
    if (!r) return 0;
    return r.rooms.reduce((acc, room) => acc + room.tables.filter(t => t.status === 'OCCUPIED' && t.isActive).length, 0);
  });

  todayReservations = computed(() =>
    this.allReservations()
      .filter(r => r.status !== 'CANCELLED')
      .sort((a, b) => (a.startTime ?? '').localeCompare(b.startTime ?? '')),
  );

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => {
      if (!account) return;
      const admin = account.authorities?.includes('ROLE_ADMIN') ?? false;
      this.isAdmin.set(admin);
      if (admin) {
        this.loadLocations();
      } else {
        this.loadMyFloorPlan();
      }
    });
    this.refreshSub = interval(1000).subscribe(() => {
      const c = this.refreshCountdown() - 1;
      if (c <= 0) {
        this.refreshCountdown.set(30);
        this.silentRefresh();
      } else {
        this.refreshCountdown.set(c);
      }
    });
  }

  ngOnDestroy(): void {
    this.refreshSub?.unsubscribe();
  }

  private loadLocations(): void {
    this.http
      .get<{ id: number; name: string; address: string }[]>(this.configService.getEndpointFor('api/locations?size=100&sort=name,asc'))
      .subscribe({
        next: locs => {
          this.locations.set(locs);
          if (locs.length > 0) {
            this.selectedLocationId.set(locs[0].id);
            this.loadFloorPlan(locs[0].id);
          }
        },
        error: () => this.error.set('Nu s-au putut încărca locațiile.'),
      });
  }

  loadMyFloorPlan(silent = false): void {
    if (!silent) this.isLoading.set(true);
    this.error.set(null);
    const url = this.configService.getEndpointFor(`api/staff/floor-plan/my?date=${this.selectedDate()}`);
    this.http.get<StaffFloorPlanResponse>(url).subscribe({
      next: res => {
        this.rawResponse.set(res);
        this.floorPlan.set(this.mapToFloorPlan(res));
        this.isLoading.set(false);
        this.lastRefreshed.set(new Date().toLocaleTimeString('ro-RO'));
        this.refreshSelectedTable(res);
        this.loadSideData(res.locationId);
      },
      error: () => {
        this.error.set('Nu s-a putut încărca planul sălii.');
        this.isLoading.set(false);
      },
    });
  }

  loadFloorPlan(locationId: number, silent = false): void {
    if (!silent) this.isLoading.set(true);
    this.error.set(null);
    const url = this.configService.getEndpointFor(`api/staff/floor-plan/${locationId}?date=${this.selectedDate()}`);
    this.http.get<StaffFloorPlanResponse>(url).subscribe({
      next: res => {
        this.rawResponse.set(res);
        this.floorPlan.set(this.mapToFloorPlan(res));
        this.isLoading.set(false);
        this.lastRefreshed.set(new Date().toLocaleTimeString('ro-RO'));
        this.refreshSelectedTable(res);
        this.loadSideData(locationId);
      },
      error: () => {
        this.error.set('Nu s-a putut încărca planul sălii.');
        this.isLoading.set(false);
      },
    });
  }

  private loadSideData(locationId: number): void {
    this.loadAllReservations(locationId);
    this.loadActiveOrders(locationId);
  }

  private loadAllReservations(locationId: number): void {
    this.isLoadingReservations.set(true);
    const date = this.selectedDate();
    const url = this.configService.getEndpointFor(
      `api/reservations?locationId.equals=${locationId}&reservationDate.equals=${date}&size=200&sort=startTime,asc`,
    );
    this.http.get<ReservationItem[]>(url).subscribe({
      next: data => {
        this.allReservations.set(data);
        this.isLoadingReservations.set(false);
      },
      error: () => this.isLoadingReservations.set(false),
    });
  }

  private loadActiveOrders(locationId: number): void {
    this.isLoadingOrders.set(true);
    const url = this.configService.getEndpointFor(`api/restaurant-orders?locationId.equals=${locationId}&size=100&sort=id,desc`);
    this.http.get<OrderItem[]>(url).subscribe({
      next: data => {
        this.activeOrders.set(data.filter(o => ['PENDING', 'CONFIRMED', 'PREPARING', 'READY'].includes(o.status)));
        this.isLoadingOrders.set(false);
      },
      error: () => this.isLoadingOrders.set(false),
    });
  }

  private silentRefresh(): void {
    if (this.isAdmin()) {
      const locId = this.selectedLocationId();
      if (locId) this.loadFloorPlan(locId, true);
    } else {
      this.loadMyFloorPlan(true);
    }
  }

  manualRefresh(): void {
    this.refreshCountdown.set(30);
    this.silentRefresh();
  }

  onDateChange(newDate: string): void {
    this.selectedDate.set(newDate);
    if (this.isAdmin()) {
      const locId = this.selectedLocationId();
      if (locId) this.loadFloorPlan(locId);
    } else {
      this.loadMyFloorPlan();
    }
  }

  onLocationChange(locationId: number): void {
    this.selectedLocationId.set(locationId);
    this.selectedTable.set(null);
    this.loadFloorPlan(locationId);
  }

  onTableSelected(table: FloorTable): void {
    const response = this.rawResponse();
    if (!response) return;
    let found: StaffTableInfo | undefined;
    for (const room of response.rooms) {
      found = room.tables.find(t => t.id === table.id);
      if (found) break;
    }
    this.selectedTable.set(found ?? null);
    this.activeTab.set('masa');
  }

  clearSelection(): void {
    this.selectedTable.set(null);
    this.activeTab.set('rezervari');
  }

  setTab(tab: SideTab): void {
    this.activeTab.set(tab);
  }

  private refreshSelectedTable(res: StaffFloorPlanResponse): void {
    const prev = this.selectedTable();
    if (prev) {
      let found: StaffTableInfo | undefined;
      for (const room of res.rooms) {
        found = room.tables.find(t => t.id === prev.id);
        if (found) break;
      }
      this.selectedTable.set(found ?? null);
    }
  }

  get locationTitle(): string {
    return this.rawResponse()?.locationName ?? 'Harta locației';
  }

  get locationAddress(): string {
    return this.rawResponse()?.locationAddress ?? '';
  }

  statusLabel(status: string): string {
    const map: Record<string, string> = {
      AVAILABLE: 'Disponibilă',
      RESERVED: 'Rezervată',
      OCCUPIED: 'Ocupată',
      OUT_OF_SERVICE: 'Indisponibilă',
      PENDING: 'În așteptare',
      CONFIRMED: 'Confirmată',
      COMPLETED: 'Finalizată',
      CANCELLED: 'Anulată',
      PREPARING: 'Se prepară',
      READY: 'Gata',
    };
    return map[status] ?? status;
  }

  statusClass(status: string): string {
    if (status === 'AVAILABLE' || status === 'COMPLETED') return 'mgr-badge mgr-badge--green';
    if (status === 'RESERVED' || status === 'PENDING' || status === 'CONFIRMED') return 'mgr-badge mgr-badge--amber';
    if (status === 'OCCUPIED' || status === 'PREPARING' || status === 'READY') return 'mgr-badge mgr-badge--blue';
    return 'mgr-badge mgr-badge--red';
  }

  private mapToFloorPlan(res: StaffFloorPlanResponse): FloorPlan {
    let canvasWidth = 800;
    let currentY = 20;
    const GAP = 24;
    const rooms = res.rooms.map(room => {
      const w = room.widthPx ?? 700;
      const h = room.heightPx ?? 400;
      const posY = currentY;
      currentY += h + GAP;
      canvasWidth = Math.max(canvasWidth, 40 + w);
      return {
        id: room.id,
        name: room.name,
        posX: 20,
        posY,
        width: w,
        height: h,
        tables: room.tables.map(t => ({
          id: t.id,
          tableNumber: t.tableNumber,
          shape: (t.shape as 'ROUND' | 'SQUARE' | 'RECTANGLE') ?? 'RECTANGLE',
          minCapacity: t.minCapacity ?? 1,
          maxCapacity: t.maxCapacity ?? 4,
          positionX: t.positionX ?? 30,
          positionY: t.positionY ?? 30,
          widthPx: t.widthPx ?? 80,
          heightPx: t.heightPx ?? 80,
          rotation: t.rotation ?? 0,
          status: (t.status as 'AVAILABLE' | 'RESERVED' | 'OCCUPIED' | 'OUT_OF_SERVICE') ?? 'AVAILABLE',
          isActive: t.isActive ?? true,
          notes: t.notes,
        })),
      };
    });
    const canvasHeight = Math.max(600, currentY + 20);
    return { canvasWidth, canvasHeight, rooms };
  }
}
