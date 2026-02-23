import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';
import { DayOfWeek } from 'app/entities/enumerations/day-of-week.model';
import { LocationHoursService } from '../service/location-hours.service';
import { ILocationHours } from '../location-hours.model';
import { LocationHoursFormGroup, LocationHoursFormService } from './location-hours-form.service';

@Component({
  selector: 'jhi-location-hours-update',
  templateUrl: './location-hours-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class LocationHoursUpdateComponent implements OnInit {
  isSaving = false;
  locationHours: ILocationHours | null = null;
  dayOfWeekValues = Object.keys(DayOfWeek);

  locationsSharedCollection: ILocation[] = [];

  protected locationHoursService = inject(LocationHoursService);
  protected locationHoursFormService = inject(LocationHoursFormService);
  protected locationService = inject(LocationService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: LocationHoursFormGroup = this.locationHoursFormService.createLocationHoursFormGroup();

  compareLocation = (o1: ILocation | null, o2: ILocation | null): boolean => this.locationService.compareLocation(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ locationHours }) => {
      this.locationHours = locationHours;
      if (locationHours) {
        this.updateForm(locationHours);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const locationHours = this.locationHoursFormService.getLocationHours(this.editForm);
    if (locationHours.id !== null) {
      this.subscribeToSaveResponse(this.locationHoursService.update(locationHours));
    } else {
      this.subscribeToSaveResponse(this.locationHoursService.create(locationHours));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILocationHours>>): void {
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

  protected updateForm(locationHours: ILocationHours): void {
    this.locationHours = locationHours;
    this.locationHoursFormService.resetForm(this.editForm, locationHours);

    this.locationsSharedCollection = this.locationService.addLocationToCollectionIfMissing<ILocation>(
      this.locationsSharedCollection,
      locationHours.location,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.locationService
      .query()
      .pipe(map((res: HttpResponse<ILocation[]>) => res.body ?? []))
      .pipe(
        map((locations: ILocation[]) =>
          this.locationService.addLocationToCollectionIfMissing<ILocation>(locations, this.locationHours?.location),
        ),
      )
      .subscribe((locations: ILocation[]) => (this.locationsSharedCollection = locations));
  }
}
