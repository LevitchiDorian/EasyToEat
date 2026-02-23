import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IMenuItem } from 'app/entities/menu-item/menu-item.model';
import { MenuItemService } from 'app/entities/menu-item/service/menu-item.service';
import { IMenuItemOption } from '../menu-item-option.model';
import { MenuItemOptionService } from '../service/menu-item-option.service';
import { MenuItemOptionFormGroup, MenuItemOptionFormService } from './menu-item-option-form.service';

@Component({
  selector: 'jhi-menu-item-option-update',
  templateUrl: './menu-item-option-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class MenuItemOptionUpdateComponent implements OnInit {
  isSaving = false;
  menuItemOption: IMenuItemOption | null = null;

  menuItemsSharedCollection: IMenuItem[] = [];

  protected menuItemOptionService = inject(MenuItemOptionService);
  protected menuItemOptionFormService = inject(MenuItemOptionFormService);
  protected menuItemService = inject(MenuItemService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MenuItemOptionFormGroup = this.menuItemOptionFormService.createMenuItemOptionFormGroup();

  compareMenuItem = (o1: IMenuItem | null, o2: IMenuItem | null): boolean => this.menuItemService.compareMenuItem(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ menuItemOption }) => {
      this.menuItemOption = menuItemOption;
      if (menuItemOption) {
        this.updateForm(menuItemOption);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const menuItemOption = this.menuItemOptionFormService.getMenuItemOption(this.editForm);
    if (menuItemOption.id !== null) {
      this.subscribeToSaveResponse(this.menuItemOptionService.update(menuItemOption));
    } else {
      this.subscribeToSaveResponse(this.menuItemOptionService.create(menuItemOption));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMenuItemOption>>): void {
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

  protected updateForm(menuItemOption: IMenuItemOption): void {
    this.menuItemOption = menuItemOption;
    this.menuItemOptionFormService.resetForm(this.editForm, menuItemOption);

    this.menuItemsSharedCollection = this.menuItemService.addMenuItemToCollectionIfMissing<IMenuItem>(
      this.menuItemsSharedCollection,
      menuItemOption.menuItem,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.menuItemService
      .query()
      .pipe(map((res: HttpResponse<IMenuItem[]>) => res.body ?? []))
      .pipe(
        map((menuItems: IMenuItem[]) =>
          this.menuItemService.addMenuItemToCollectionIfMissing<IMenuItem>(menuItems, this.menuItemOption?.menuItem),
        ),
      )
      .subscribe((menuItems: IMenuItem[]) => (this.menuItemsSharedCollection = menuItems));
  }
}
