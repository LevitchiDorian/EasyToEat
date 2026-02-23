import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IRestaurantTable } from 'app/entities/restaurant-table/restaurant-table.model';
import { RestaurantTableService } from 'app/entities/restaurant-table/service/restaurant-table.service';
import { IPromotion } from 'app/entities/promotion/promotion.model';
import { PromotionService } from 'app/entities/promotion/service/promotion.service';
import { IReservation } from 'app/entities/reservation/reservation.model';
import { ReservationService } from 'app/entities/reservation/service/reservation.service';
import { OrderStatus } from 'app/entities/enumerations/order-status.model';
import { RestaurantOrderService } from '../service/restaurant-order.service';
import { IRestaurantOrder } from '../restaurant-order.model';
import { RestaurantOrderFormGroup, RestaurantOrderFormService } from './restaurant-order-form.service';

@Component({
  selector: 'jhi-restaurant-order-update',
  templateUrl: './restaurant-order-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class RestaurantOrderUpdateComponent implements OnInit {
  isSaving = false;
  restaurantOrder: IRestaurantOrder | null = null;
  orderStatusValues = Object.keys(OrderStatus);

  locationsSharedCollection: ILocation[] = [];
  usersSharedCollection: IUser[] = [];
  restaurantTablesSharedCollection: IRestaurantTable[] = [];
  promotionsSharedCollection: IPromotion[] = [];
  reservationsSharedCollection: IReservation[] = [];

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected restaurantOrderService = inject(RestaurantOrderService);
  protected restaurantOrderFormService = inject(RestaurantOrderFormService);
  protected locationService = inject(LocationService);
  protected userService = inject(UserService);
  protected restaurantTableService = inject(RestaurantTableService);
  protected promotionService = inject(PromotionService);
  protected reservationService = inject(ReservationService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: RestaurantOrderFormGroup = this.restaurantOrderFormService.createRestaurantOrderFormGroup();

  compareLocation = (o1: ILocation | null, o2: ILocation | null): boolean => this.locationService.compareLocation(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareRestaurantTable = (o1: IRestaurantTable | null, o2: IRestaurantTable | null): boolean =>
    this.restaurantTableService.compareRestaurantTable(o1, o2);

  comparePromotion = (o1: IPromotion | null, o2: IPromotion | null): boolean => this.promotionService.comparePromotion(o1, o2);

  compareReservation = (o1: IReservation | null, o2: IReservation | null): boolean => this.reservationService.compareReservation(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ restaurantOrder }) => {
      this.restaurantOrder = restaurantOrder;
      if (restaurantOrder) {
        this.updateForm(restaurantOrder);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('restaurantApp.error', { ...err, key: `error.file.${err.key}` })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const restaurantOrder = this.restaurantOrderFormService.getRestaurantOrder(this.editForm);
    if (restaurantOrder.id !== null) {
      this.subscribeToSaveResponse(this.restaurantOrderService.update(restaurantOrder));
    } else {
      this.subscribeToSaveResponse(this.restaurantOrderService.create(restaurantOrder));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRestaurantOrder>>): void {
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

  protected updateForm(restaurantOrder: IRestaurantOrder): void {
    this.restaurantOrder = restaurantOrder;
    this.restaurantOrderFormService.resetForm(this.editForm, restaurantOrder);

    this.locationsSharedCollection = this.locationService.addLocationToCollectionIfMissing<ILocation>(
      this.locationsSharedCollection,
      restaurantOrder.location,
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      restaurantOrder.client,
      restaurantOrder.assignedWaiter,
    );
    this.restaurantTablesSharedCollection = this.restaurantTableService.addRestaurantTableToCollectionIfMissing<IRestaurantTable>(
      this.restaurantTablesSharedCollection,
      restaurantOrder.table,
    );
    this.promotionsSharedCollection = this.promotionService.addPromotionToCollectionIfMissing<IPromotion>(
      this.promotionsSharedCollection,
      restaurantOrder.promotion,
    );
    this.reservationsSharedCollection = this.reservationService.addReservationToCollectionIfMissing<IReservation>(
      this.reservationsSharedCollection,
      restaurantOrder.reservation,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.locationService
      .query()
      .pipe(map((res: HttpResponse<ILocation[]>) => res.body ?? []))
      .pipe(
        map((locations: ILocation[]) =>
          this.locationService.addLocationToCollectionIfMissing<ILocation>(locations, this.restaurantOrder?.location),
        ),
      )
      .subscribe((locations: ILocation[]) => (this.locationsSharedCollection = locations));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(users, this.restaurantOrder?.client, this.restaurantOrder?.assignedWaiter),
        ),
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.restaurantTableService
      .query()
      .pipe(map((res: HttpResponse<IRestaurantTable[]>) => res.body ?? []))
      .pipe(
        map((restaurantTables: IRestaurantTable[]) =>
          this.restaurantTableService.addRestaurantTableToCollectionIfMissing<IRestaurantTable>(
            restaurantTables,
            this.restaurantOrder?.table,
          ),
        ),
      )
      .subscribe((restaurantTables: IRestaurantTable[]) => (this.restaurantTablesSharedCollection = restaurantTables));

    this.promotionService
      .query()
      .pipe(map((res: HttpResponse<IPromotion[]>) => res.body ?? []))
      .pipe(
        map((promotions: IPromotion[]) =>
          this.promotionService.addPromotionToCollectionIfMissing<IPromotion>(promotions, this.restaurantOrder?.promotion),
        ),
      )
      .subscribe((promotions: IPromotion[]) => (this.promotionsSharedCollection = promotions));

    this.reservationService
      .query()
      .pipe(map((res: HttpResponse<IReservation[]>) => res.body ?? []))
      .pipe(
        map((reservations: IReservation[]) =>
          this.reservationService.addReservationToCollectionIfMissing<IReservation>(reservations, this.restaurantOrder?.reservation),
        ),
      )
      .subscribe((reservations: IReservation[]) => (this.reservationsSharedCollection = reservations));
  }
}
