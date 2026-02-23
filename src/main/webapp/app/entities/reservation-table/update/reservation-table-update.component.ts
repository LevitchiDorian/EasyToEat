import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IRestaurantTable } from 'app/entities/restaurant-table/restaurant-table.model';
import { RestaurantTableService } from 'app/entities/restaurant-table/service/restaurant-table.service';
import { IReservation } from 'app/entities/reservation/reservation.model';
import { ReservationService } from 'app/entities/reservation/service/reservation.service';
import { ReservationTableService } from '../service/reservation-table.service';
import { IReservationTable } from '../reservation-table.model';
import { ReservationTableFormGroup, ReservationTableFormService } from './reservation-table-form.service';

@Component({
  selector: 'jhi-reservation-table-update',
  templateUrl: './reservation-table-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ReservationTableUpdateComponent implements OnInit {
  isSaving = false;
  reservationTable: IReservationTable | null = null;

  restaurantTablesSharedCollection: IRestaurantTable[] = [];
  reservationsSharedCollection: IReservation[] = [];

  protected reservationTableService = inject(ReservationTableService);
  protected reservationTableFormService = inject(ReservationTableFormService);
  protected restaurantTableService = inject(RestaurantTableService);
  protected reservationService = inject(ReservationService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ReservationTableFormGroup = this.reservationTableFormService.createReservationTableFormGroup();

  compareRestaurantTable = (o1: IRestaurantTable | null, o2: IRestaurantTable | null): boolean =>
    this.restaurantTableService.compareRestaurantTable(o1, o2);

  compareReservation = (o1: IReservation | null, o2: IReservation | null): boolean => this.reservationService.compareReservation(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ reservationTable }) => {
      this.reservationTable = reservationTable;
      if (reservationTable) {
        this.updateForm(reservationTable);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const reservationTable = this.reservationTableFormService.getReservationTable(this.editForm);
    if (reservationTable.id !== null) {
      this.subscribeToSaveResponse(this.reservationTableService.update(reservationTable));
    } else {
      this.subscribeToSaveResponse(this.reservationTableService.create(reservationTable));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IReservationTable>>): void {
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

  protected updateForm(reservationTable: IReservationTable): void {
    this.reservationTable = reservationTable;
    this.reservationTableFormService.resetForm(this.editForm, reservationTable);

    this.restaurantTablesSharedCollection = this.restaurantTableService.addRestaurantTableToCollectionIfMissing<IRestaurantTable>(
      this.restaurantTablesSharedCollection,
      reservationTable.table,
    );
    this.reservationsSharedCollection = this.reservationService.addReservationToCollectionIfMissing<IReservation>(
      this.reservationsSharedCollection,
      reservationTable.reservation,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.restaurantTableService
      .query()
      .pipe(map((res: HttpResponse<IRestaurantTable[]>) => res.body ?? []))
      .pipe(
        map((restaurantTables: IRestaurantTable[]) =>
          this.restaurantTableService.addRestaurantTableToCollectionIfMissing<IRestaurantTable>(
            restaurantTables,
            this.reservationTable?.table,
          ),
        ),
      )
      .subscribe((restaurantTables: IRestaurantTable[]) => (this.restaurantTablesSharedCollection = restaurantTables));

    this.reservationService
      .query()
      .pipe(map((res: HttpResponse<IReservation[]>) => res.body ?? []))
      .pipe(
        map((reservations: IReservation[]) =>
          this.reservationService.addReservationToCollectionIfMissing<IReservation>(reservations, this.reservationTable?.reservation),
        ),
      )
      .subscribe((reservations: IReservation[]) => (this.reservationsSharedCollection = reservations));
  }
}
