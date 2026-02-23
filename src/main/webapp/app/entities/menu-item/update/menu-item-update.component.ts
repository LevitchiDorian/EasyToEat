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
import { IMenuCategory } from 'app/entities/menu-category/menu-category.model';
import { MenuCategoryService } from 'app/entities/menu-category/service/menu-category.service';
import { MenuItemService } from '../service/menu-item.service';
import { IMenuItem } from '../menu-item.model';
import { MenuItemFormGroup, MenuItemFormService } from './menu-item-form.service';

@Component({
  selector: 'jhi-menu-item-update',
  templateUrl: './menu-item-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class MenuItemUpdateComponent implements OnInit {
  isSaving = false;
  menuItem: IMenuItem | null = null;

  menuCategoriesSharedCollection: IMenuCategory[] = [];

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected menuItemService = inject(MenuItemService);
  protected menuItemFormService = inject(MenuItemFormService);
  protected menuCategoryService = inject(MenuCategoryService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MenuItemFormGroup = this.menuItemFormService.createMenuItemFormGroup();

  compareMenuCategory = (o1: IMenuCategory | null, o2: IMenuCategory | null): boolean =>
    this.menuCategoryService.compareMenuCategory(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ menuItem }) => {
      this.menuItem = menuItem;
      if (menuItem) {
        this.updateForm(menuItem);
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
    const menuItem = this.menuItemFormService.getMenuItem(this.editForm);
    if (menuItem.id !== null) {
      this.subscribeToSaveResponse(this.menuItemService.update(menuItem));
    } else {
      this.subscribeToSaveResponse(this.menuItemService.create(menuItem));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMenuItem>>): void {
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

  protected updateForm(menuItem: IMenuItem): void {
    this.menuItem = menuItem;
    this.menuItemFormService.resetForm(this.editForm, menuItem);

    this.menuCategoriesSharedCollection = this.menuCategoryService.addMenuCategoryToCollectionIfMissing<IMenuCategory>(
      this.menuCategoriesSharedCollection,
      menuItem.category,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.menuCategoryService
      .query()
      .pipe(map((res: HttpResponse<IMenuCategory[]>) => res.body ?? []))
      .pipe(
        map((menuCategories: IMenuCategory[]) =>
          this.menuCategoryService.addMenuCategoryToCollectionIfMissing<IMenuCategory>(menuCategories, this.menuItem?.category),
        ),
      )
      .subscribe((menuCategories: IMenuCategory[]) => (this.menuCategoriesSharedCollection = menuCategories));
  }
}
