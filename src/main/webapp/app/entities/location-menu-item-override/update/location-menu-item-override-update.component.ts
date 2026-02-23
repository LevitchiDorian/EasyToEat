import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IMenuItem } from 'app/entities/menu-item/menu-item.model';
import { MenuItemService } from 'app/entities/menu-item/service/menu-item.service';
import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';
import { LocationMenuItemOverrideService } from '../service/location-menu-item-override.service';
import { ILocationMenuItemOverride } from '../location-menu-item-override.model';
import { LocationMenuItemOverrideFormGroup, LocationMenuItemOverrideFormService } from './location-menu-item-override-form.service';

@Component({
  selector: 'jhi-location-menu-item-override-update',
  templateUrl: './location-menu-item-override-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class LocationMenuItemOverrideUpdateComponent implements OnInit {
  isSaving = false;
  locationMenuItemOverride: ILocationMenuItemOverride | null = null;

  menuItemsSharedCollection: IMenuItem[] = [];
  locationsSharedCollection: ILocation[] = [];

  protected locationMenuItemOverrideService = inject(LocationMenuItemOverrideService);
  protected locationMenuItemOverrideFormService = inject(LocationMenuItemOverrideFormService);
  protected menuItemService = inject(MenuItemService);
  protected locationService = inject(LocationService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: LocationMenuItemOverrideFormGroup = this.locationMenuItemOverrideFormService.createLocationMenuItemOverrideFormGroup();

  compareMenuItem = (o1: IMenuItem | null, o2: IMenuItem | null): boolean => this.menuItemService.compareMenuItem(o1, o2);

  compareLocation = (o1: ILocation | null, o2: ILocation | null): boolean => this.locationService.compareLocation(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ locationMenuItemOverride }) => {
      this.locationMenuItemOverride = locationMenuItemOverride;
      if (locationMenuItemOverride) {
        this.updateForm(locationMenuItemOverride);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const locationMenuItemOverride = this.locationMenuItemOverrideFormService.getLocationMenuItemOverride(this.editForm);
    if (locationMenuItemOverride.id !== null) {
      this.subscribeToSaveResponse(this.locationMenuItemOverrideService.update(locationMenuItemOverride));
    } else {
      this.subscribeToSaveResponse(this.locationMenuItemOverrideService.create(locationMenuItemOverride));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILocationMenuItemOverride>>): void {
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

  protected updateForm(locationMenuItemOverride: ILocationMenuItemOverride): void {
    this.locationMenuItemOverride = locationMenuItemOverride;
    this.locationMenuItemOverrideFormService.resetForm(this.editForm, locationMenuItemOverride);

    this.menuItemsSharedCollection = this.menuItemService.addMenuItemToCollectionIfMissing<IMenuItem>(
      this.menuItemsSharedCollection,
      locationMenuItemOverride.menuItem,
    );
    this.locationsSharedCollection = this.locationService.addLocationToCollectionIfMissing<ILocation>(
      this.locationsSharedCollection,
      locationMenuItemOverride.location,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.menuItemService
      .query()
      .pipe(map((res: HttpResponse<IMenuItem[]>) => res.body ?? []))
      .pipe(
        map((menuItems: IMenuItem[]) =>
          this.menuItemService.addMenuItemToCollectionIfMissing<IMenuItem>(menuItems, this.locationMenuItemOverride?.menuItem),
        ),
      )
      .subscribe((menuItems: IMenuItem[]) => (this.menuItemsSharedCollection = menuItems));

    this.locationService
      .query()
      .pipe(map((res: HttpResponse<ILocation[]>) => res.body ?? []))
      .pipe(
        map((locations: ILocation[]) =>
          this.locationService.addLocationToCollectionIfMissing<ILocation>(locations, this.locationMenuItemOverride?.location),
        ),
      )
      .subscribe((locations: ILocation[]) => (this.locationsSharedCollection = locations));
  }
}
