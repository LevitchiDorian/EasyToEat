import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IDiningRoom } from 'app/entities/dining-room/dining-room.model';
import { DiningRoomService } from 'app/entities/dining-room/service/dining-room.service';
import { WaitingListService } from '../service/waiting-list.service';
import { IWaitingList } from '../waiting-list.model';
import { WaitingListFormGroup, WaitingListFormService } from './waiting-list-form.service';

@Component({
  selector: 'jhi-waiting-list-update',
  templateUrl: './waiting-list-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class WaitingListUpdateComponent implements OnInit {
  isSaving = false;
  waitingList: IWaitingList | null = null;

  locationsSharedCollection: ILocation[] = [];
  usersSharedCollection: IUser[] = [];
  diningRoomsSharedCollection: IDiningRoom[] = [];

  protected waitingListService = inject(WaitingListService);
  protected waitingListFormService = inject(WaitingListFormService);
  protected locationService = inject(LocationService);
  protected userService = inject(UserService);
  protected diningRoomService = inject(DiningRoomService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: WaitingListFormGroup = this.waitingListFormService.createWaitingListFormGroup();

  compareLocation = (o1: ILocation | null, o2: ILocation | null): boolean => this.locationService.compareLocation(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareDiningRoom = (o1: IDiningRoom | null, o2: IDiningRoom | null): boolean => this.diningRoomService.compareDiningRoom(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ waitingList }) => {
      this.waitingList = waitingList;
      if (waitingList) {
        this.updateForm(waitingList);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const waitingList = this.waitingListFormService.getWaitingList(this.editForm);
    if (waitingList.id !== null) {
      this.subscribeToSaveResponse(this.waitingListService.update(waitingList));
    } else {
      this.subscribeToSaveResponse(this.waitingListService.create(waitingList));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IWaitingList>>): void {
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

  protected updateForm(waitingList: IWaitingList): void {
    this.waitingList = waitingList;
    this.waitingListFormService.resetForm(this.editForm, waitingList);

    this.locationsSharedCollection = this.locationService.addLocationToCollectionIfMissing<ILocation>(
      this.locationsSharedCollection,
      waitingList.location,
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, waitingList.client);
    this.diningRoomsSharedCollection = this.diningRoomService.addDiningRoomToCollectionIfMissing<IDiningRoom>(
      this.diningRoomsSharedCollection,
      waitingList.room,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.locationService
      .query()
      .pipe(map((res: HttpResponse<ILocation[]>) => res.body ?? []))
      .pipe(
        map((locations: ILocation[]) =>
          this.locationService.addLocationToCollectionIfMissing<ILocation>(locations, this.waitingList?.location),
        ),
      )
      .subscribe((locations: ILocation[]) => (this.locationsSharedCollection = locations));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.waitingList?.client)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.diningRoomService
      .query()
      .pipe(map((res: HttpResponse<IDiningRoom[]>) => res.body ?? []))
      .pipe(
        map((diningRooms: IDiningRoom[]) =>
          this.diningRoomService.addDiningRoomToCollectionIfMissing<IDiningRoom>(diningRooms, this.waitingList?.room),
        ),
      )
      .subscribe((diningRooms: IDiningRoom[]) => (this.diningRoomsSharedCollection = diningRooms));
  }
}
