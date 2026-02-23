import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';
import { IDiningRoom } from '../dining-room.model';
import { DiningRoomService } from '../service/dining-room.service';
import { DiningRoomFormGroup, DiningRoomFormService } from './dining-room-form.service';

@Component({
  selector: 'jhi-dining-room-update',
  templateUrl: './dining-room-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class DiningRoomUpdateComponent implements OnInit {
  isSaving = false;
  diningRoom: IDiningRoom | null = null;

  locationsSharedCollection: ILocation[] = [];

  protected diningRoomService = inject(DiningRoomService);
  protected diningRoomFormService = inject(DiningRoomFormService);
  protected locationService = inject(LocationService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DiningRoomFormGroup = this.diningRoomFormService.createDiningRoomFormGroup();

  compareLocation = (o1: ILocation | null, o2: ILocation | null): boolean => this.locationService.compareLocation(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ diningRoom }) => {
      this.diningRoom = diningRoom;
      if (diningRoom) {
        this.updateForm(diningRoom);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const diningRoom = this.diningRoomFormService.getDiningRoom(this.editForm);
    if (diningRoom.id !== null) {
      this.subscribeToSaveResponse(this.diningRoomService.update(diningRoom));
    } else {
      this.subscribeToSaveResponse(this.diningRoomService.create(diningRoom));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDiningRoom>>): void {
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

  protected updateForm(diningRoom: IDiningRoom): void {
    this.diningRoom = diningRoom;
    this.diningRoomFormService.resetForm(this.editForm, diningRoom);

    this.locationsSharedCollection = this.locationService.addLocationToCollectionIfMissing<ILocation>(
      this.locationsSharedCollection,
      diningRoom.location,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.locationService
      .query()
      .pipe(map((res: HttpResponse<ILocation[]>) => res.body ?? []))
      .pipe(
        map((locations: ILocation[]) =>
          this.locationService.addLocationToCollectionIfMissing<ILocation>(locations, this.diningRoom?.location),
        ),
      )
      .subscribe((locations: ILocation[]) => (this.locationsSharedCollection = locations));
  }
}
