import { Component, inject, signal, computed, OnInit, OnDestroy, HostListener, ChangeDetectionStrategy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { AccountService } from 'app/core/auth/account.service';
import { StaffFloorPlanResponse, LocationOption } from 'app/admin/staff-floor-plan/staff-floor-plan.component';

interface EditorDecoration {
  id: string;
  label: string;
  posX: number;
  posY: number;
  width: number;
  height: number;
  type: 'PLANT' | 'WALL' | 'PILLAR' | 'OTHER';
}

interface EditorTable {
  id: number;
  tableNumber: string;
  shape: 'ROUND' | 'SQUARE' | 'RECTANGLE';
  minCapacity: number;
  maxCapacity: number;
  positionX: number;
  positionY: number;
  widthPx: number;
  heightPx: number;
  rotation: number;
  isActive: boolean;
  notes?: string;
  roomId: number;
  isDirty: boolean;
  isNew: boolean;
  isOverlapping?: boolean;
}

interface EditorRoom {
  id: number;
  name: string;
  widthPx: number;
  heightPx: number;
  tables: EditorTable[];
  decorations: EditorDecoration[];
  decorationsDirty: boolean;
}

interface DragState {
  tableId: number;
  roomId: number;
  startX: number;
  startY: number;
  origX: number;
  origY: number;
}

interface ResizeState {
  tableId: number;
  roomId: number;
  startX: number;
  startY: number;
  origW: number;
  origH: number;
}

interface DecorationDragState {
  decoId: string;
  roomId: number;
  startX: number;
  startY: number;
  origX: number;
  origY: number;
}

interface DecorationResizeState {
  decoId: string;
  roomId: number;
  startX: number;
  startY: number;
  origW: number;
  origH: number;
}

@Component({
  selector: 'app-manager-floor-plan-editor',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, FormsModule],
  templateUrl: './manager-floor-plan-editor.component.html',
  styleUrl: './manager-floor-plan-editor.component.scss',
})
export default class ManagerFloorPlanEditorComponent implements OnInit, OnDestroy {
  private readonly http = inject(HttpClient);
  private readonly configService = inject(ApplicationConfigService);
  private readonly accountService = inject(AccountService);

  isAdmin = signal(false);
  isLoading = signal(false);
  isSaving = signal(false);
  error = signal<string | null>(null);
  saveSuccess = signal(false);

  locations = signal<LocationOption[]>([]);
  selectedLocationId = signal<number | null>(null);
  locationName = signal('');

  rooms = signal<EditorRoom[]>([]);
  selectedTableId = signal<number | null>(null);
  selectedDecorationId = signal<string | null>(null);

  showAddModal = signal(false);
  addModalRoomId = signal<number | null>(null);

  showAddDecoModal = signal(false);
  addDecoModalRoomId = signal<number | null>(null);

  newTableForm: {
    tableNumber: string;
    shape: 'ROUND' | 'SQUARE' | 'RECTANGLE';
    minCapacity: number;
    maxCapacity: number;
    widthPx: number;
    heightPx: number;
  } = this.emptyNewTableForm();

  newDecoForm: { label: string; type: 'PLANT' | 'WALL' | 'PILLAR' | 'OTHER'; width: number; height: number } = this.emptyNewDecoForm();

  selectedTableEdits: Partial<EditorTable> = {};
  selectedDecoEdits: Partial<EditorDecoration> = {};

  snapToGrid = signal(true);
  gridSize = signal(10);

  private dragState: DragState | null = null;
  private resizeState: ResizeState | null = null;
  private decoDragState: DecorationDragState | null = null;
  private decoResizeState: DecorationResizeState | null = null;
  private nextTempId = -1;

  isDirty = computed(() => this.rooms().some(r => r.tables.some(t => t.isDirty || t.isNew) || r.decorationsDirty));

  selectedTable = computed(() => {
    const id = this.selectedTableId();
    if (id == null) return null;
    for (const room of this.rooms()) {
      const t = room.tables.find(t => t.id === id);
      if (t) return t;
    }
    return null;
  });

  selectedDecoration = computed(() => {
    const id = this.selectedDecorationId();
    if (!id) return null;
    for (const room of this.rooms()) {
      const d = room.decorations.find(d => d.id === id);
      if (d) return d;
    }
    return null;
  });

  activeTablesInRoom(roomId: number): EditorTable[] {
    const room = this.rooms().find(r => r.id === roomId);
    return room?.tables.filter(t => t.isActive) ?? [];
  }

  decorationsInRoom(roomId: number): EditorDecoration[] {
    return this.rooms().find(r => r.id === roomId)?.decorations ?? [];
  }

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

  ngOnDestroy(): void {}

  private loadLocations(): void {
    this.isLoading.set(true);
    this.http
      .get<{ id: number; name: string; address: string }[]>(this.configService.getEndpointFor('api/locations?size=100&sort=name,asc'))
      .subscribe({
        next: locs => {
          this.locations.set(locs);
          if (locs.length > 0) {
            this.selectedLocationId.set(locs[0].id);
            this.loadFloorPlan(locs[0].id);
          } else {
            this.isLoading.set(false);
          }
        },
        error: () => {
          this.error.set('Nu s-au putut încărca locațiile.');
          this.isLoading.set(false);
        },
      });
  }

  loadMyFloorPlan(): void {
    this.isLoading.set(true);
    this.error.set(null);
    const url = this.configService.getEndpointFor(`api/staff/floor-plan/my?date=${new Date().toISOString().substring(0, 10)}`);
    this.http.get<StaffFloorPlanResponse>(url).subscribe({
      next: res => {
        if (!res.locationId) {
          this.error.set('Contul tău nu are o locație asignată.');
          this.isLoading.set(false);
          return;
        }
        this.selectedLocationId.set(res.locationId);
        this.locationName.set(res.locationName ?? '');
        this.rooms.set(this.mapToEditorRooms(res));
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
    const today = new Date().toISOString().substring(0, 10);
    const url = this.configService.getEndpointFor(`api/staff/floor-plan/${locationId}?date=${today}`);
    this.http.get<StaffFloorPlanResponse>(url).subscribe({
      next: res => {
        this.locationName.set(res.locationName ?? '');
        this.rooms.set(this.mapToEditorRooms(res));
        this.selectedTableId.set(null);
        this.selectedDecorationId.set(null);
        this.isLoading.set(false);
      },
      error: () => {
        this.error.set('Nu s-a putut încărca planul sălii.');
        this.isLoading.set(false);
      },
    });
  }

  onLocationChange(locId: number): void {
    this.selectedLocationId.set(+locId);
    this.selectedTableId.set(null);
    this.selectedDecorationId.set(null);
    this.loadFloorPlan(+locId);
  }

  // ── Table Drag ───────────────────────────────────────────────────────────

  onTableMouseDown(e: MouseEvent, table: EditorTable, room: EditorRoom): void {
    if (e.button !== 0) return;
    e.preventDefault();
    e.stopPropagation();
    this.dragState = {
      tableId: table.id,
      roomId: room.id,
      startX: e.clientX,
      startY: e.clientY,
      origX: table.positionX,
      origY: table.positionY,
    };
    this.selectTable(table);
  }

  onResizeMouseDown(e: MouseEvent, table: EditorTable, room: EditorRoom): void {
    if (e.button !== 0) return;
    e.preventDefault();
    e.stopPropagation();
    this.resizeState = {
      tableId: table.id,
      roomId: room.id,
      startX: e.clientX,
      startY: e.clientY,
      origW: table.widthPx,
      origH: table.heightPx,
    };
  }

  // ── Decoration Drag ──────────────────────────────────────────────────────

  onDecoMouseDown(e: MouseEvent, deco: EditorDecoration, room: EditorRoom): void {
    if (e.button !== 0) return;
    e.preventDefault();
    e.stopPropagation();
    this.decoDragState = {
      decoId: deco.id,
      roomId: room.id,
      startX: e.clientX,
      startY: e.clientY,
      origX: deco.posX,
      origY: deco.posY,
    };
    this.selectDecoration(deco);
  }

  onDecoResizeMouseDown(e: MouseEvent, deco: EditorDecoration, room: EditorRoom): void {
    if (e.button !== 0) return;
    e.preventDefault();
    e.stopPropagation();
    this.decoResizeState = {
      decoId: deco.id,
      roomId: room.id,
      startX: e.clientX,
      startY: e.clientY,
      origW: deco.width,
      origH: deco.height,
    };
  }

  @HostListener('document:mousemove', ['$event'])
  onDocMouseMove(e: MouseEvent): void {
    if (!this.dragState && !this.resizeState && !this.decoDragState && !this.decoResizeState) return;

    if (this.dragState) {
      const { tableId, roomId, startX, startY, origX, origY } = this.dragState;
      const room = this.rooms().find(r => r.id === roomId);
      if (!room) return;
      const table = room.tables.find(t => t.id === tableId);
      if (!table) return;

      const dx = e.clientX - startX;
      const dy = e.clientY - startY;
      let newX = Math.max(0, Math.min(origX + dx, room.widthPx - table.widthPx));
      let newY = Math.max(0, Math.min(origY + dy, room.heightPx - table.heightPx));
      if (this.snapToGrid()) {
        newX = this.snap(newX);
        newY = this.snap(newY);
      }

      const overlapping = this.checkOverlap(room, tableId, newX, newY, table.widthPx, table.heightPx);

      this.rooms.update(rooms =>
        rooms.map(r =>
          r.id !== roomId
            ? r
            : {
                ...r,
                tables: r.tables.map(t =>
                  t.id !== tableId ? t : { ...t, positionX: newX, positionY: newY, isDirty: true, isOverlapping: overlapping },
                ),
              },
        ),
      );
      this.syncEditsFromTable(tableId);
    }

    if (this.resizeState) {
      const { tableId, roomId, startX, startY, origW, origH } = this.resizeState;
      const room = this.rooms().find(r => r.id === roomId);
      const table = room?.tables.find(t => t.id === tableId);
      if (!room || !table) return;

      const dx = e.clientX - startX;
      const dy = e.clientY - startY;
      let newW = Math.max(40, origW + dx);
      let newH = Math.max(40, origH + dy);
      if (this.snapToGrid()) {
        newW = Math.max(40, this.snap(newW));
        newH = Math.max(40, this.snap(newH));
      }

      const overlapping = this.checkOverlap(room, tableId, table.positionX, table.positionY, newW, newH);

      this.rooms.update(rooms =>
        rooms.map(r =>
          r.id !== roomId
            ? r
            : {
                ...r,
                tables: r.tables.map(t =>
                  t.id !== tableId ? t : { ...t, widthPx: newW, heightPx: newH, isDirty: true, isOverlapping: overlapping },
                ),
              },
        ),
      );
      this.syncEditsFromTable(tableId);
    }

    if (this.decoDragState) {
      const { decoId, roomId, startX, startY, origX, origY } = this.decoDragState;
      const room = this.rooms().find(r => r.id === roomId);
      const deco = room?.decorations.find(d => d.id === decoId);
      if (!room || !deco) return;

      const dx = e.clientX - startX;
      const dy = e.clientY - startY;
      let newX = Math.max(0, Math.min(origX + dx, room.widthPx - deco.width));
      let newY = Math.max(0, Math.min(origY + dy, room.heightPx - deco.height));
      if (this.snapToGrid()) {
        newX = this.snap(newX);
        newY = this.snap(newY);
      }

      this.rooms.update(rooms =>
        rooms.map(r =>
          r.id !== roomId
            ? r
            : {
                ...r,
                decorationsDirty: true,
                decorations: r.decorations.map(d => (d.id !== decoId ? d : { ...d, posX: newX, posY: newY })),
              },
        ),
      );
      this.syncDecoEditsFromDeco(decoId);
    }

    if (this.decoResizeState) {
      const { decoId, roomId, startX, startY, origW, origH } = this.decoResizeState;
      const room = this.rooms().find(r => r.id === roomId);
      if (!room) return;

      const dx = e.clientX - startX;
      const dy = e.clientY - startY;
      let newW = Math.max(20, origW + dx);
      let newH = Math.max(20, origH + dy);
      if (this.snapToGrid()) {
        newW = Math.max(20, this.snap(newW));
        newH = Math.max(20, this.snap(newH));
      }

      this.rooms.update(rooms =>
        rooms.map(r =>
          r.id !== roomId
            ? r
            : {
                ...r,
                decorationsDirty: true,
                decorations: r.decorations.map(d => (d.id !== decoId ? d : { ...d, width: newW, height: newH })),
              },
        ),
      );
      this.syncDecoEditsFromDeco(decoId);
    }
  }

  @HostListener('document:mouseup')
  onDocMouseUp(): void {
    if (this.dragState) {
      const { tableId, roomId, origX, origY } = this.dragState;
      const room = this.rooms().find(r => r.id === roomId);
      const table = room?.tables.find(t => t.id === tableId);
      if (table?.isOverlapping) {
        this.rooms.update(rooms =>
          rooms.map(r =>
            r.id !== roomId
              ? r
              : {
                  ...r,
                  tables: r.tables.map(t => (t.id !== tableId ? t : { ...t, positionX: origX, positionY: origY, isOverlapping: false })),
                },
          ),
        );
        this.syncEditsFromTable(tableId);
      } else if (table) {
        this.rooms.update(rooms =>
          rooms.map(r =>
            r.id !== roomId ? r : { ...r, tables: r.tables.map(t => (t.id !== tableId ? t : { ...t, isOverlapping: false })) },
          ),
        );
      }
    }
    if (this.resizeState) {
      const { tableId, roomId, origW, origH } = this.resizeState;
      const room = this.rooms().find(r => r.id === roomId);
      const table = room?.tables.find(t => t.id === tableId);
      if (table?.isOverlapping) {
        this.rooms.update(rooms =>
          rooms.map(r =>
            r.id !== roomId
              ? r
              : {
                  ...r,
                  tables: r.tables.map(t => (t.id !== tableId ? t : { ...t, widthPx: origW, heightPx: origH, isOverlapping: false })),
                },
          ),
        );
        this.syncEditsFromTable(tableId);
      } else if (table) {
        this.rooms.update(rooms =>
          rooms.map(r =>
            r.id !== roomId ? r : { ...r, tables: r.tables.map(t => (t.id !== tableId ? t : { ...t, isOverlapping: false })) },
          ),
        );
      }
    }
    this.dragState = null;
    this.resizeState = null;
    this.decoDragState = null;
    this.decoResizeState = null;
  }

  private snap(val: number): number {
    const g = this.gridSize();
    return Math.round(val / g) * g;
  }

  private checkOverlap(room: EditorRoom, selfId: number, x: number, y: number, w: number, h: number): boolean {
    // Check against other active tables (strict: touching is allowed, only actual overlap)
    for (const other of room.tables) {
      if (other.id === selfId || !other.isActive) continue;
      const ox = other.positionX,
        oy = other.positionY,
        ow = other.widthPx,
        oh = other.heightPx;
      if (x < ox + ow && x + w > ox && y < oy + oh && y + h > oy) return true;
    }
    // Check against decorations (tables cannot overlap obstacles)
    for (const deco of room.decorations) {
      const ox = deco.posX,
        oy = deco.posY,
        ow = deco.width,
        oh = deco.height;
      if (x < ox + ow && x + w > ox && y < oy + oh && y + h > oy) return true;
    }
    return false;
  }

  // ── Table Selection & Editing ────────────────────────────────────────────

  selectTable(table: EditorTable): void {
    this.selectedTableId.set(table.id);
    this.selectedDecorationId.set(null);
    this.selectedTableEdits = {
      tableNumber: table.tableNumber,
      shape: table.shape,
      minCapacity: table.minCapacity,
      maxCapacity: table.maxCapacity,
      widthPx: table.widthPx,
      heightPx: table.heightPx,
      positionX: table.positionX,
      positionY: table.positionY,
      rotation: table.rotation,
      isActive: table.isActive,
    };
  }

  private syncEditsFromTable(tableId: number): void {
    if (this.selectedTableId() !== tableId) return;
    const t = this.selectedTable();
    if (!t) return;
    this.selectedTableEdits = {
      ...this.selectedTableEdits,
      positionX: t.positionX,
      positionY: t.positionY,
      widthPx: t.widthPx,
      heightPx: t.heightPx,
    };
  }

  applyEdit(tableId: number): void {
    this.rooms.update(rooms =>
      rooms.map(r => ({
        ...r,
        tables: r.tables.map(t =>
          t.id !== tableId
            ? t
            : {
                ...t,
                tableNumber: this.selectedTableEdits.tableNumber ?? t.tableNumber,
                shape: (this.selectedTableEdits.shape ?? t.shape) as 'ROUND' | 'SQUARE' | 'RECTANGLE',
                minCapacity: this.selectedTableEdits.minCapacity ?? t.minCapacity,
                maxCapacity: this.selectedTableEdits.maxCapacity ?? t.maxCapacity,
                rotation: this.selectedTableEdits.rotation ?? t.rotation,
                isActive: this.selectedTableEdits.isActive ?? t.isActive,
                isDirty: true,
              },
        ),
      })),
    );
  }

  applyEditSize(tableId: number): void {
    this.rooms.update(rooms =>
      rooms.map(r => ({
        ...r,
        tables: r.tables.map(t =>
          t.id !== tableId
            ? t
            : {
                ...t,
                widthPx: this.selectedTableEdits.widthPx ?? t.widthPx,
                heightPx: this.selectedTableEdits.heightPx ?? t.heightPx,
                isDirty: true,
              },
        ),
      })),
    );
  }

  applyEditPos(tableId: number): void {
    this.rooms.update(rooms =>
      rooms.map(r => ({
        ...r,
        tables: r.tables.map(t =>
          t.id !== tableId
            ? t
            : {
                ...t,
                positionX: this.selectedTableEdits.positionX ?? t.positionX,
                positionY: this.selectedTableEdits.positionY ?? t.positionY,
                isDirty: true,
              },
        ),
      })),
    );
  }

  // ── Decoration Selection & Editing ───────────────────────────────────────

  selectDecoration(deco: EditorDecoration): void {
    this.selectedDecorationId.set(deco.id);
    this.selectedTableId.set(null);
    this.selectedDecoEdits = {
      label: deco.label,
      type: deco.type,
      posX: deco.posX,
      posY: deco.posY,
      width: deco.width,
      height: deco.height,
    };
  }

  private syncDecoEditsFromDeco(decoId: string): void {
    if (this.selectedDecorationId() !== decoId) return;
    const d = this.selectedDecoration();
    if (!d) return;
    this.selectedDecoEdits = { ...this.selectedDecoEdits, posX: d.posX, posY: d.posY, width: d.width, height: d.height };
  }

  applyDecoEdit(decoId: string): void {
    this.rooms.update(rooms =>
      rooms.map(r => ({
        ...r,
        decorationsDirty: r.decorations.some(d => d.id === decoId) ? true : r.decorationsDirty,
        decorations: r.decorations.map(d =>
          d.id !== decoId
            ? d
            : {
                ...d,
                label: this.selectedDecoEdits.label ?? d.label,
                type: (this.selectedDecoEdits.type ?? d.type) as EditorDecoration['type'],
              },
        ),
      })),
    );
  }

  applyDecoEditSize(decoId: string): void {
    this.rooms.update(rooms =>
      rooms.map(r => ({
        ...r,
        decorationsDirty: r.decorations.some(d => d.id === decoId) ? true : r.decorationsDirty,
        decorations: r.decorations.map(d =>
          d.id !== decoId ? d : { ...d, width: this.selectedDecoEdits.width ?? d.width, height: this.selectedDecoEdits.height ?? d.height },
        ),
      })),
    );
  }

  applyDecoEditPos(decoId: string): void {
    this.rooms.update(rooms =>
      rooms.map(r => ({
        ...r,
        decorationsDirty: r.decorations.some(d => d.id === decoId) ? true : r.decorationsDirty,
        decorations: r.decorations.map(d =>
          d.id !== decoId ? d : { ...d, posX: this.selectedDecoEdits.posX ?? d.posX, posY: this.selectedDecoEdits.posY ?? d.posY },
        ),
      })),
    );
  }

  // ── Add Table ────────────────────────────────────────────────────────────

  openAddTable(roomId: number): void {
    this.addModalRoomId.set(roomId);
    this.newTableForm = this.emptyNewTableForm();
    this.showAddModal.set(true);
  }

  closeAddModal(): void {
    this.showAddModal.set(false);
    this.addModalRoomId.set(null);
  }

  confirmAddTable(): void {
    const roomId = this.addModalRoomId();
    if (!roomId || !this.newTableForm.tableNumber) return;

    const newTable: EditorTable = {
      id: this.nextTempId--,
      tableNumber: this.newTableForm.tableNumber,
      shape: this.newTableForm.shape,
      minCapacity: this.newTableForm.minCapacity,
      maxCapacity: this.newTableForm.maxCapacity,
      positionX: 20,
      positionY: 20,
      widthPx: this.newTableForm.widthPx,
      heightPx: this.newTableForm.heightPx,
      rotation: 0,
      isActive: true,
      roomId,
      isDirty: false,
      isNew: true,
    };

    this.rooms.update(rooms => rooms.map(r => (r.id !== roomId ? r : { ...r, tables: [...r.tables, newTable] })));
    this.closeAddModal();
    this.selectTable(newTable);
  }

  deleteTable(table: EditorTable): void {
    if (table.isNew) {
      this.rooms.update(rooms => rooms.map(r => ({ ...r, tables: r.tables.filter(t => t.id !== table.id) })));
      this.selectedTableId.set(null);
    } else {
      this.rooms.update(rooms =>
        rooms.map(r => ({
          ...r,
          tables: r.tables.map(t => (t.id !== table.id ? t : { ...t, isActive: false, isDirty: true })),
        })),
      );
      this.selectedTableId.set(null);
    }
  }

  // ── Add/Delete Decoration ────────────────────────────────────────────────

  openAddDeco(roomId: number): void {
    this.addDecoModalRoomId.set(roomId);
    this.newDecoForm = this.emptyNewDecoForm();
    this.showAddDecoModal.set(true);
  }

  closeAddDecoModal(): void {
    this.showAddDecoModal.set(false);
    this.addDecoModalRoomId.set(null);
  }

  confirmAddDeco(): void {
    const roomId = this.addDecoModalRoomId();
    if (!roomId || !this.newDecoForm.label) return;

    const newDeco: EditorDecoration = {
      id: 'deco_' + Date.now() + '_' + Math.floor(Math.random() * 10000),
      label: this.newDecoForm.label,
      type: this.newDecoForm.type,
      posX: 20,
      posY: 20,
      width: this.newDecoForm.width,
      height: this.newDecoForm.height,
    };

    this.rooms.update(rooms =>
      rooms.map(r => (r.id !== roomId ? r : { ...r, decorations: [...r.decorations, newDeco], decorationsDirty: true })),
    );
    this.closeAddDecoModal();
    this.selectDecoration(newDeco);
  }

  deleteDeco(deco: EditorDecoration): void {
    this.rooms.update(rooms =>
      rooms.map(r => ({
        ...r,
        decorationsDirty: r.decorations.some(d => d.id === deco.id) ? true : r.decorationsDirty,
        decorations: r.decorations.filter(d => d.id !== deco.id),
      })),
    );
    this.selectedDecorationId.set(null);
  }

  // ── Save ──────────────────────────────────────────────────────────────────

  saveAll(): void {
    this.isSaving.set(true);
    this.saveSuccess.set(false);

    const requests: ReturnType<typeof this.http.patch>[] = [];

    for (const room of this.rooms()) {
      // Save table changes
      for (const table of room.tables) {
        if (table.isNew) {
          const body = {
            tableNumber: table.tableNumber,
            shape: table.shape,
            minCapacity: table.minCapacity,
            maxCapacity: table.maxCapacity,
            positionX: Math.round(table.positionX),
            positionY: Math.round(table.positionY),
            widthPx: Math.round(table.widthPx),
            heightPx: Math.round(table.heightPx),
            rotation: table.rotation,
            status: 'AVAILABLE',
            isActive: true,
            room: { id: room.id },
          };
          requests.push(
            this.http.post<any>(this.configService.getEndpointFor('api/restaurant-tables'), body).pipe(catchError(() => of(null))),
          );
        } else if (table.isDirty) {
          const body = {
            id: table.id,
            tableNumber: table.tableNumber,
            shape: table.shape,
            minCapacity: table.minCapacity,
            maxCapacity: table.maxCapacity,
            positionX: Math.round(table.positionX),
            positionY: Math.round(table.positionY),
            widthPx: Math.round(table.widthPx),
            heightPx: Math.round(table.heightPx),
            rotation: table.rotation,
            isActive: table.isActive,
          };
          requests.push(
            this.http
              .patch<any>(this.configService.getEndpointFor(`api/restaurant-tables/${table.id}`), body)
              .pipe(catchError(() => of(null))),
          );
        }
      }
      // Save decoration changes
      if (room.decorationsDirty) {
        const decorationsJson = JSON.stringify(room.decorations);
        requests.push(
          this.http
            .patch<any>(this.configService.getEndpointFor(`api/dining-rooms/${room.id}`), { id: room.id, decorationsJson })
            .pipe(catchError(() => of(null))),
        );
      }
    }

    if (requests.length === 0) {
      this.isSaving.set(false);
      return;
    }

    forkJoin(requests).subscribe({
      next: () => {
        this.isSaving.set(false);
        this.saveSuccess.set(true);
        const locId = this.selectedLocationId();
        if (locId) {
          if (this.isAdmin()) {
            this.loadFloorPlan(locId);
          } else {
            this.loadMyFloorPlan();
          }
        }
        setTimeout(() => this.saveSuccess.set(false), 3000);
      },
      error: () => {
        this.isSaving.set(false);
        this.error.set('Eroare la salvare. Unele modificări nu au fost salvate.');
      },
    });
  }

  resetChanges(): void {
    const locId = this.selectedLocationId();
    if (!locId) return;
    this.selectedTableId.set(null);
    this.selectedDecorationId.set(null);
    if (this.isAdmin()) {
      this.loadFloorPlan(locId);
    } else {
      this.loadMyFloorPlan();
    }
  }

  // ── Helpers ───────────────────────────────────────────────────────────────

  private mapToEditorRooms(res: StaffFloorPlanResponse): EditorRoom[] {
    return res.rooms.map(room => {
      let decorations: EditorDecoration[] = [];
      const rawJson = (room as any).decorationsJson as string | undefined;
      if (rawJson) {
        try {
          decorations = JSON.parse(rawJson) ?? [];
        } catch {
          decorations = [];
        }
      }
      return {
        id: room.id,
        name: room.name,
        widthPx: room.widthPx ?? 700,
        heightPx: room.heightPx ?? 400,
        decorations,
        decorationsDirty: false,
        tables: room.tables.map(t => ({
          id: t.id,
          tableNumber: t.tableNumber,
          shape: (t.shape as 'ROUND' | 'SQUARE' | 'RECTANGLE') ?? 'RECTANGLE',
          minCapacity: t.minCapacity ?? 1,
          maxCapacity: t.maxCapacity ?? 4,
          positionX: t.positionX ?? 20,
          positionY: t.positionY ?? 20,
          widthPx: t.widthPx ?? 80,
          heightPx: t.heightPx ?? 80,
          rotation: t.rotation ?? 0,
          isActive: t.isActive ?? true,
          notes: t.notes,
          roomId: room.id,
          isDirty: false,
          isNew: false,
        })),
      };
    });
  }

  private emptyNewTableForm() {
    return { tableNumber: '', shape: 'RECTANGLE' as const, minCapacity: 2, maxCapacity: 4, widthPx: 80, heightPx: 80 };
  }

  private emptyNewDecoForm() {
    return { label: '', type: 'OTHER' as const, width: 60, height: 60 };
  }

  shapeLabel(shape: string): string {
    return shape === 'ROUND' ? 'Rotundă' : shape === 'SQUARE' ? 'Pătrată' : 'Dreptunghiulară';
  }

  decoTypeLabel(type: string): string {
    const labels: Record<string, string> = { PLANT: 'Plantă', WALL: 'Perete', PILLAR: 'Stâlp', OTHER: 'Altul' };
    return labels[type] ?? type;
  }
}
