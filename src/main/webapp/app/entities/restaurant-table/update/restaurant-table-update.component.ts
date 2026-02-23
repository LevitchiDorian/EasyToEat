import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IDiningRoom } from 'app/entities/dining-room/dining-room.model';
import { DiningRoomService } from 'app/entities/dining-room/service/dining-room.service';
import { TableShape } from 'app/entities/enumerations/table-shape.model';
import { TableStatus } from 'app/entities/enumerations/table-status.model';
import { RestaurantTableService } from '../service/restaurant-table.service';
import { IRestaurantTable } from '../restaurant-table.model';
import { RestaurantTableFormGroup, RestaurantTableFormService } from './restaurant-table-form.service';

@Component({
  selector: 'jhi-restaurant-table-update',
  templateUrl: './restaurant-table-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class RestaurantTableUpdateComponent implements OnInit {
  isSaving = false;
  restaurantTable: IRestaurantTable | null = null;
  tableShapeValues = Object.keys(TableShape);
  tableStatusValues = Object.keys(TableStatus);

  diningRoomsSharedCollection: IDiningRoom[] = [];

  protected restaurantTableService = inject(RestaurantTableService);
  protected restaurantTableFormService = inject(RestaurantTableFormService);
  protected diningRoomService = inject(DiningRoomService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: RestaurantTableFormGroup = this.restaurantTableFormService.createRestaurantTableFormGroup();

  compareDiningRoom = (o1: IDiningRoom | null, o2: IDiningRoom | null): boolean => this.diningRoomService.compareDiningRoom(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ restaurantTable }) => {
      this.restaurantTable = restaurantTable;
      if (restaurantTable) {
        this.updateForm(restaurantTable);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const restaurantTable = this.restaurantTableFormService.getRestaurantTable(this.editForm);
    if (restaurantTable.id !== null) {
      this.subscribeToSaveResponse(this.restaurantTableService.update(restaurantTable));
    } else {
      this.subscribeToSaveResponse(this.restaurantTableService.create(restaurantTable));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRestaurantTable>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(restaurantTable: IRestaurantTable): void {
    this.restaurantTable = restaurantTable;
    this.restaurantTableFormService.resetForm(this.editForm, restaurantTable);

    this.diningRoomsSharedCollection = this.diningRoomService.addDiningRoomToCollectionIfMissing<IDiningRoom>(
      this.diningRoomsSharedCollection,
      restaurantTable.room,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.diningRoomService
      .query()
      .pipe(map((res: HttpResponse<IDiningRoom[]>) => res.body ?? []))
      .pipe(
        map((diningRooms: IDiningRoom[]) =>
          this.diningRoomService.addDiningRoomToCollectionIfMissing<IDiningRoom>(diningRooms, this.restaurantTable?.room),
        ),
      )
      .subscribe((diningRooms: IDiningRoom[]) => (this.diningRoomsSharedCollection = diningRooms));
  }
}
