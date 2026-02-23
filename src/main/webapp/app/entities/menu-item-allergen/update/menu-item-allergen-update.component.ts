import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IMenuItem } from 'app/entities/menu-item/menu-item.model';
import { MenuItemService } from 'app/entities/menu-item/service/menu-item.service';
import { AllergenType } from 'app/entities/enumerations/allergen-type.model';
import { MenuItemAllergenService } from '../service/menu-item-allergen.service';
import { IMenuItemAllergen } from '../menu-item-allergen.model';
import { MenuItemAllergenFormGroup, MenuItemAllergenFormService } from './menu-item-allergen-form.service';

@Component({
  selector: 'jhi-menu-item-allergen-update',
  templateUrl: './menu-item-allergen-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class MenuItemAllergenUpdateComponent implements OnInit {
  isSaving = false;
  menuItemAllergen: IMenuItemAllergen | null = null;
  allergenTypeValues = Object.keys(AllergenType);

  menuItemsSharedCollection: IMenuItem[] = [];

  protected menuItemAllergenService = inject(MenuItemAllergenService);
  protected menuItemAllergenFormService = inject(MenuItemAllergenFormService);
  protected menuItemService = inject(MenuItemService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MenuItemAllergenFormGroup = this.menuItemAllergenFormService.createMenuItemAllergenFormGroup();

  compareMenuItem = (o1: IMenuItem | null, o2: IMenuItem | null): boolean => this.menuItemService.compareMenuItem(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ menuItemAllergen }) => {
      this.menuItemAllergen = menuItemAllergen;
      if (menuItemAllergen) {
        this.updateForm(menuItemAllergen);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const menuItemAllergen = this.menuItemAllergenFormService.getMenuItemAllergen(this.editForm);
    if (menuItemAllergen.id !== null) {
      this.subscribeToSaveResponse(this.menuItemAllergenService.update(menuItemAllergen));
    } else {
      this.subscribeToSaveResponse(this.menuItemAllergenService.create(menuItemAllergen));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMenuItemAllergen>>): void {
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

  protected updateForm(menuItemAllergen: IMenuItemAllergen): void {
    this.menuItemAllergen = menuItemAllergen;
    this.menuItemAllergenFormService.resetForm(this.editForm, menuItemAllergen);

    this.menuItemsSharedCollection = this.menuItemService.addMenuItemToCollectionIfMissing<IMenuItem>(
      this.menuItemsSharedCollection,
      menuItemAllergen.menuItem,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.menuItemService
      .query()
      .pipe(map((res: HttpResponse<IMenuItem[]>) => res.body ?? []))
      .pipe(
        map((menuItems: IMenuItem[]) =>
          this.menuItemService.addMenuItemToCollectionIfMissing<IMenuItem>(menuItems, this.menuItemAllergen?.menuItem),
        ),
      )
      .subscribe((menuItems: IMenuItem[]) => (this.menuItemsSharedCollection = menuItems));
  }
}
