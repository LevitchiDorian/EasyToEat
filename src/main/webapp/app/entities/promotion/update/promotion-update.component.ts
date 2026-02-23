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
import { IBrand } from 'app/entities/brand/brand.model';
import { BrandService } from 'app/entities/brand/service/brand.service';
import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';
import { DiscountType } from 'app/entities/enumerations/discount-type.model';
import { PromotionService } from '../service/promotion.service';
import { IPromotion } from '../promotion.model';
import { PromotionFormGroup, PromotionFormService } from './promotion-form.service';

@Component({
  selector: 'jhi-promotion-update',
  templateUrl: './promotion-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PromotionUpdateComponent implements OnInit {
  isSaving = false;
  promotion: IPromotion | null = null;
  discountTypeValues = Object.keys(DiscountType);

  brandsSharedCollection: IBrand[] = [];
  locationsSharedCollection: ILocation[] = [];

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected promotionService = inject(PromotionService);
  protected promotionFormService = inject(PromotionFormService);
  protected brandService = inject(BrandService);
  protected locationService = inject(LocationService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PromotionFormGroup = this.promotionFormService.createPromotionFormGroup();

  compareBrand = (o1: IBrand | null, o2: IBrand | null): boolean => this.brandService.compareBrand(o1, o2);

  compareLocation = (o1: ILocation | null, o2: ILocation | null): boolean => this.locationService.compareLocation(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ promotion }) => {
      this.promotion = promotion;
      if (promotion) {
        this.updateForm(promotion);
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
    const promotion = this.promotionFormService.getPromotion(this.editForm);
    if (promotion.id !== null) {
      this.subscribeToSaveResponse(this.promotionService.update(promotion));
    } else {
      this.subscribeToSaveResponse(this.promotionService.create(promotion));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPromotion>>): void {
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

  protected updateForm(promotion: IPromotion): void {
    this.promotion = promotion;
    this.promotionFormService.resetForm(this.editForm, promotion);

    this.brandsSharedCollection = this.brandService.addBrandToCollectionIfMissing<IBrand>(this.brandsSharedCollection, promotion.brand);
    this.locationsSharedCollection = this.locationService.addLocationToCollectionIfMissing<ILocation>(
      this.locationsSharedCollection,
      promotion.location,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.brandService
      .query()
      .pipe(map((res: HttpResponse<IBrand[]>) => res.body ?? []))
      .pipe(map((brands: IBrand[]) => this.brandService.addBrandToCollectionIfMissing<IBrand>(brands, this.promotion?.brand)))
      .subscribe((brands: IBrand[]) => (this.brandsSharedCollection = brands));

    this.locationService
      .query()
      .pipe(map((res: HttpResponse<ILocation[]>) => res.body ?? []))
      .pipe(
        map((locations: ILocation[]) =>
          this.locationService.addLocationToCollectionIfMissing<ILocation>(locations, this.promotion?.location),
        ),
      )
      .subscribe((locations: ILocation[]) => (this.locationsSharedCollection = locations));
  }
}
