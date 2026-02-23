import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IBrand } from 'app/entities/brand/brand.model';
import { BrandService } from 'app/entities/brand/service/brand.service';
import { IMenuCategory } from '../menu-category.model';
import { MenuCategoryService } from '../service/menu-category.service';
import { MenuCategoryFormGroup, MenuCategoryFormService } from './menu-category-form.service';

@Component({
  selector: 'jhi-menu-category-update',
  templateUrl: './menu-category-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class MenuCategoryUpdateComponent implements OnInit {
  isSaving = false;
  menuCategory: IMenuCategory | null = null;

  menuCategoriesSharedCollection: IMenuCategory[] = [];
  brandsSharedCollection: IBrand[] = [];

  protected menuCategoryService = inject(MenuCategoryService);
  protected menuCategoryFormService = inject(MenuCategoryFormService);
  protected brandService = inject(BrandService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MenuCategoryFormGroup = this.menuCategoryFormService.createMenuCategoryFormGroup();

  compareMenuCategory = (o1: IMenuCategory | null, o2: IMenuCategory | null): boolean =>
    this.menuCategoryService.compareMenuCategory(o1, o2);

  compareBrand = (o1: IBrand | null, o2: IBrand | null): boolean => this.brandService.compareBrand(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ menuCategory }) => {
      this.menuCategory = menuCategory;
      if (menuCategory) {
        this.updateForm(menuCategory);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const menuCategory = this.menuCategoryFormService.getMenuCategory(this.editForm);
    if (menuCategory.id !== null) {
      this.subscribeToSaveResponse(this.menuCategoryService.update(menuCategory));
    } else {
      this.subscribeToSaveResponse(this.menuCategoryService.create(menuCategory));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMenuCategory>>): void {
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

  protected updateForm(menuCategory: IMenuCategory): void {
    this.menuCategory = menuCategory;
    this.menuCategoryFormService.resetForm(this.editForm, menuCategory);

    this.menuCategoriesSharedCollection = this.menuCategoryService.addMenuCategoryToCollectionIfMissing<IMenuCategory>(
      this.menuCategoriesSharedCollection,
      menuCategory.parent,
    );
    this.brandsSharedCollection = this.brandService.addBrandToCollectionIfMissing<IBrand>(this.brandsSharedCollection, menuCategory.brand);
  }

  protected loadRelationshipsOptions(): void {
    this.menuCategoryService
      .query()
      .pipe(map((res: HttpResponse<IMenuCategory[]>) => res.body ?? []))
      .pipe(
        map((menuCategories: IMenuCategory[]) =>
          this.menuCategoryService.addMenuCategoryToCollectionIfMissing<IMenuCategory>(menuCategories, this.menuCategory?.parent),
        ),
      )
      .subscribe((menuCategories: IMenuCategory[]) => (this.menuCategoriesSharedCollection = menuCategories));

    this.brandService
      .query()
      .pipe(map((res: HttpResponse<IBrand[]>) => res.body ?? []))
      .pipe(map((brands: IBrand[]) => this.brandService.addBrandToCollectionIfMissing<IBrand>(brands, this.menuCategory?.brand)))
      .subscribe((brands: IBrand[]) => (this.brandsSharedCollection = brands));
  }
}
