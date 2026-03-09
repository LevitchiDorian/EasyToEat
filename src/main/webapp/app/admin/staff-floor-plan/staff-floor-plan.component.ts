import { Component, inject, signal, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { AccountService } from 'app/core/auth/account.service';
import { FloorMapComponent, FloorPlan, FloorTable } from 'app/public/floor-map/floor-map.component';

export interface StaffReservationInfo {
  id: number;
  reservationCode: string;
  reservationDate: string;
  startTime: string;
  endTime: string;
  partySize: number;
  status: string;
  specialRequests?: string;
  clientLogin?: string;
  clientFirstName?: string;
  clientLastName?: string;
}

export interface StaffTableInfo {
  id: number;
  tableNumber: string;
  shape: string;
  minCapacity: number;
  maxCapacity: number;
  positionX: number;
  positionY: number;
  widthPx: number;
  heightPx: number;
  rotation: number;
  status: string;
  isActive: boolean;
  notes?: string;
  reservation?: StaffReservationInfo;
}

export interface StaffRoomInfo {
  id: number;
  name: string;
  floor?: number;
  widthPx: number;
  heightPx: number;
  tables: StaffTableInfo[];
}

export interface StaffFloorPlanResponse {
  locationId: number;
  locationName: string;
  locationAddress: string;
  date: string;
  rooms: StaffRoomInfo[];
}

export interface LocationOption {
  id: number;
  name: string;
  address: string;
}

@Component({
  selector: 'app-staff-floor-plan',
  standalone: true,
  templateUrl: './staff-floor-plan.component.html',
  styleUrl: './staff-floor-plan.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, FormsModule, FloorMapComponent],
})
export default class StaffFloorPlanComponent implements OnInit {
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

  panelOpen = signal(false);
  selectedTable = signal<StaffTableInfo | null>(null);

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

  loadMyFloorPlan(): void {
    this.isLoading.set(true);
    this.error.set(null);
    const url = this.configService.getEndpointFor(`api/staff/floor-plan/my?date=${this.selectedDate()}`);
    this.http.get<StaffFloorPlanResponse>(url).subscribe({
      next: res => {
        this.rawResponse.set(res);
        this.floorPlan.set(this.mapToFloorPlan(res));
        this.isLoading.set(false);
      },
      error: () => {
        this.error.set('Nu s-a putut încărca planul sălii.');
        this.isLoading.set(false);
      },
    });
  }

  loadFloorPlan(locationId: number): void {
    this.isLoading.set(true);
    this.error.set(null);
    this.panelOpen.set(false);
    this.selectedTable.set(null);
    const url = this.configService.getEndpointFor(`api/staff/floor-plan/${locationId}?date=${this.selectedDate()}`);
    this.http.get<StaffFloorPlanResponse>(url).subscribe({
      next: res => {
        this.rawResponse.set(res);
        this.floorPlan.set(this.mapToFloorPlan(res));
        this.isLoading.set(false);
      },
      error: () => {
        this.error.set('Nu s-a putut încărca planul sălii.');
        this.isLoading.set(false);
      },
    });
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
    this.panelOpen.set(true);
  }

  closePanel(): void {
    this.panelOpen.set(false);
    this.selectedTable.set(null);
  }

  get locationTitle(): string {
    const r = this.rawResponse();
    return r ? r.locationName : 'Planul sălii';
  }

  get locationAddress(): string {
    const r = this.rawResponse();
    return r ? r.locationAddress : '';
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
    };
    return map[status] ?? status;
  }

  statusBadgeClass(status: string): string {
    if (status === 'AVAILABLE') return 'rv-status-badge rv-status-badge--success';
    if (status === 'RESERVED' || status === 'PENDING') return 'rv-status-badge rv-status-badge--warning';
    if (status === 'OCCUPIED' || status === 'CONFIRMED') return 'rv-status-badge rv-status-badge--blue';
    if (status === 'COMPLETED') return 'rv-status-badge rv-status-badge--success';
    return 'rv-status-badge rv-status-badge--danger';
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
      canvasWidth = Math.max(canvasWidth, 20 + w + 20);
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
