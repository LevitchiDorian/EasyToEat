import { Component, Input, Output, EventEmitter, OnInit, OnDestroy, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';

export type TableStatus = 'AVAILABLE' | 'RESERVED' | 'OCCUPIED' | 'OUT_OF_SERVICE';
export type TableShape = 'ROUND' | 'SQUARE' | 'RECTANGLE';

export interface FloorTable {
  id: number;
  tableNumber: string;
  shape: TableShape;
  minCapacity: number;
  maxCapacity: number;
  positionX: number;
  positionY: number;
  widthPx: number;
  heightPx: number;
  rotation: number;
  status: TableStatus;
  isActive: boolean;
  notes?: string;
}

export interface FloorRoom {
  id: number;
  name: string;
  posX: number;
  posY: number;
  width: number;
  height: number;
  tables: FloorTable[];
}

export interface FloorPlan {
  canvasWidth: number;
  canvasHeight: number;
  rooms: FloorRoom[];
  staticAreas?: { name: string; posX: number; posY: number; width: number; height: number; icon?: string }[];
}

// Mock floor plan matching the design screenshot
export const DEMO_FLOOR_PLAN: FloorPlan = {
  canvasWidth: 900,
  canvasHeight: 560,
  rooms: [
    {
      id: 1,
      name: 'Sushi Bar',
      posX: 20,
      posY: 20,
      width: 400,
      height: 200,
      tables: [
        {
          id: 1,
          tableNumber: 'S1',
          shape: 'ROUND',
          minCapacity: 1,
          maxCapacity: 2,
          positionX: 60,
          positionY: 80,
          widthPx: 70,
          heightPx: 70,
          rotation: 0,
          status: 'AVAILABLE',
          isActive: true,
        },
        {
          id: 2,
          tableNumber: 'S2',
          shape: 'ROUND',
          minCapacity: 1,
          maxCapacity: 2,
          positionX: 150,
          positionY: 80,
          widthPx: 70,
          heightPx: 70,
          rotation: 0,
          status: 'OCCUPIED',
          isActive: true,
        },
        {
          id: 3,
          tableNumber: 'S3',
          shape: 'ROUND',
          minCapacity: 1,
          maxCapacity: 2,
          positionX: 240,
          positionY: 80,
          widthPx: 70,
          heightPx: 70,
          rotation: 0,
          status: 'AVAILABLE',
          isActive: true,
        },
        {
          id: 4,
          tableNumber: 'S4',
          shape: 'ROUND',
          minCapacity: 1,
          maxCapacity: 2,
          positionX: 330,
          positionY: 80,
          widthPx: 70,
          heightPx: 70,
          rotation: 0,
          status: 'AVAILABLE',
          isActive: true,
        },
      ],
    },
    {
      id: 2,
      name: 'Dining',
      posX: 20,
      posY: 240,
      width: 760,
      height: 300,
      tables: [
        {
          id: 5,
          tableNumber: 'M1',
          shape: 'RECTANGLE',
          minCapacity: 2,
          maxCapacity: 4,
          positionX: 30,
          positionY: 50,
          widthPx: 100,
          heightPx: 90,
          rotation: 0,
          status: 'AVAILABLE',
          isActive: true,
        },
        {
          id: 6,
          tableNumber: 'M2',
          shape: 'RECTANGLE',
          minCapacity: 2,
          maxCapacity: 4,
          positionX: 150,
          positionY: 50,
          widthPx: 100,
          heightPx: 90,
          rotation: 0,
          status: 'RESERVED',
          isActive: true,
        },
        {
          id: 7,
          tableNumber: 'M3',
          shape: 'RECTANGLE',
          minCapacity: 2,
          maxCapacity: 6,
          positionX: 270,
          positionY: 50,
          widthPx: 130,
          heightPx: 90,
          rotation: 0,
          status: 'AVAILABLE',
          isActive: true,
        },
        {
          id: 8,
          tableNumber: 'M4',
          shape: 'RECTANGLE',
          minCapacity: 2,
          maxCapacity: 4,
          positionX: 420,
          positionY: 50,
          widthPx: 100,
          heightPx: 90,
          rotation: 0,
          status: 'AVAILABLE',
          isActive: true,
        },
        {
          id: 9,
          tableNumber: 'M5',
          shape: 'RECTANGLE',
          minCapacity: 2,
          maxCapacity: 4,
          positionX: 540,
          positionY: 50,
          widthPx: 100,
          heightPx: 90,
          rotation: 0,
          status: 'AVAILABLE',
          isActive: true,
        },
        {
          id: 10,
          tableNumber: 'M6',
          shape: 'RECTANGLE',
          minCapacity: 4,
          maxCapacity: 8,
          positionX: 30,
          positionY: 170,
          widthPx: 160,
          heightPx: 90,
          rotation: 0,
          status: 'AVAILABLE',
          isActive: true,
        },
      ],
    },
  ],
  staticAreas: [
    { name: 'Bucătărie', posX: 450, posY: 20, width: 430, height: 200, icon: 'kitchen' },
    { name: 'Intrare', posX: 730, posY: 490, width: 60, height: 60, icon: 'door' },
  ],
};

@Component({
  selector: 'app-floor-map',
  templateUrl: './floor-map.component.html',
  styleUrl: './floor-map.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule],
})
export class FloorMapComponent implements OnInit, OnDestroy {
  @Input() floorPlan: FloorPlan = DEMO_FLOOR_PLAN;
  @Output() tableSelected = new EventEmitter<FloorTable>();

  scale = signal(1);
  panX = signal(0);
  panY = signal(0);
  selectedTable = signal<FloorTable | null>(null);
  tooltipTable = signal<FloorTable | null>(null);

  private isDragging = false;
  private dragStartX = 0;
  private dragStartY = 0;
  private dragStartPanX = 0;
  private dragStartPanY = 0;

  readonly MIN_SCALE = 0.4;
  readonly MAX_SCALE = 2.5;

  ngOnInit(): void {}
  ngOnDestroy(): void {}

  get transform(): string {
    return `translate(${this.panX()}px, ${this.panY()}px) scale(${this.scale()})`;
  }

  zoomIn(): void {
    this.scale.update(s => Math.min(s + 0.2, this.MAX_SCALE));
  }

  zoomOut(): void {
    this.scale.update(s => Math.max(s - 0.2, this.MIN_SCALE));
  }

  resetView(): void {
    this.scale.set(1);
    this.panX.set(0);
    this.panY.set(0);
  }

  onWheel(event: WheelEvent): void {
    event.preventDefault();
    const delta = event.deltaY > 0 ? -0.1 : 0.1;
    this.scale.update(s => Math.min(Math.max(s + delta, this.MIN_SCALE), this.MAX_SCALE));
  }

  onMouseDown(event: MouseEvent): void {
    if ((event.target as HTMLElement).closest('.rv-table')) return;
    this.isDragging = true;
    this.dragStartX = event.clientX;
    this.dragStartY = event.clientY;
    this.dragStartPanX = this.panX();
    this.dragStartPanY = this.panY();
    event.preventDefault();
  }

  onMouseMove(event: MouseEvent): void {
    if (!this.isDragging) return;
    this.panX.set(this.dragStartPanX + event.clientX - this.dragStartX);
    this.panY.set(this.dragStartPanY + event.clientY - this.dragStartY);
  }

  onMouseUp(): void {
    this.isDragging = false;
  }

  selectTable(table: FloorTable, event: Event): void {
    event.stopPropagation();
    if (table.status === 'OCCUPIED' || table.status === 'OUT_OF_SERVICE') return;
    this.selectedTable.set(this.selectedTable()?.id === table.id ? null : table);
    this.tableSelected.emit(table);
  }

  showTooltip(table: FloorTable): void {
    this.tooltipTable.set(table);
  }

  hideTooltip(): void {
    this.tooltipTable.set(null);
  }

  tableClass(table: FloorTable): string {
    const base = 'rv-table';
    const shape = table.shape === 'ROUND' ? 'rv-table--round' : 'rv-table--rect';
    const status =
      table.status === 'AVAILABLE'
        ? 'rv-table--available'
        : table.status === 'RESERVED'
          ? 'rv-table--reserved'
          : table.status === 'OCCUPIED'
            ? 'rv-table--occupied'
            : 'rv-table--inactive';
    const selected = this.selectedTable()?.id === table.id ? 'rv-table--selected' : '';
    return [base, shape, status, selected].filter(Boolean).join(' ');
  }

  tableStyle(table: FloorTable): Record<string, string> {
    return {
      left: `${table.positionX}px`,
      top: `${table.positionY}px`,
      width: `${table.widthPx}px`,
      height: `${table.heightPx}px`,
      transform: table.rotation ? `rotate(${table.rotation}deg)` : '',
    };
  }

  statusLabel(status: TableStatus): string {
    const map: Record<TableStatus, string> = {
      AVAILABLE: 'Disponibilă',
      RESERVED: 'Rezervată',
      OCCUPIED: 'Ocupată',
      OUT_OF_SERVICE: 'Indisponibilă',
    };
    return map[status] ?? status;
  }
}
