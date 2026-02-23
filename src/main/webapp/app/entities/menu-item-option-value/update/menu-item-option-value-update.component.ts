import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IMenuItemOption } from 'app/entities/menu-item-option/menu-item-option.model';
import { MenuItemOptionService } from 'app/entities/menu-item-option/service/menu-item-option.service';
import { IMenuItemOptionValue } from '../menu-item-option-value.model';
import { MenuItemOptionValueService } from '../service/menu-item-option-value.service';
import { MenuItemOptionValueFormGroup, MenuItemOptionValueFormService } from './menu-item-option-value-form.service';

@Component({
  selector: 'jhi-menu-item-option-value-update',
  templateUrl: './menu-item-option-value-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class MenuItemOptionValueUpdateComponent implements OnInit {
  isSaving = false;
  menuItemOptionValue: IMenuItemOptionValue | null = null;

  menuItemOptionsSharedCollection: IMenuItemOption[] = [];

  protected menuItemOptionValueService = inject(MenuItemOptionValueService);
  protected menuItemOptionValueFormService = inject(MenuItemOptionValueFormService);
  protected menuItemOptionService = inject(MenuItemOptionService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MenuItemOptionValueFormGroup = this.menuItemOptionValueFormService.createMenuItemOptionValueFormGroup();

  compareMenuItemOption = (o1: IMenuItemOption | null, o2: IMenuItemOption | null): boolean =>
    this.menuItemOptionService.compareMenuItemOption(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ menuItemOptionValue }) => {
      this.menuItemOptionValue = menuItemOptionValue;
      if (menuItemOptionValue) {
        this.updateForm(menuItemOptionValue);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const menuItemOptionValue = this.menuItemOptionValueFormService.getMenuItemOptionValue(this.editForm);
    if (menuItemOptionValue.id !== null) {
      this.subscribeToSaveResponse(this.menuItemOptionValueService.update(menuItemOptionValue));
    } else {
      this.subscribeToSaveResponse(this.menuItemOptionValueService.create(menuItemOptionValue));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMenuItemOptionValue>>): void {
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

  protected updateForm(menuItemOptionValue: IMenuItemOptionValue): void {
    this.menuItemOptionValue = menuItemOptionValue;
    this.menuItemOptionValueFormService.resetForm(this.editForm, menuItemOptionValue);

    this.menuItemOptionsSharedCollection = this.menuItemOptionService.addMenuItemOptionToCollectionIfMissing<IMenuItemOption>(
      this.menuItemOptionsSharedCollection,
      menuItemOptionValue.option,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.menuItemOptionService
      .query()
      .pipe(map((res: HttpResponse<IMenuItemOption[]>) => res.body ?? []))
      .pipe(
        map((menuItemOptions: IMenuItemOption[]) =>
          this.menuItemOptionService.addMenuItemOptionToCollectionIfMissing<IMenuItemOption>(
            menuItemOptions,
            this.menuItemOptionValue?.option,
          ),
        ),
      )
      .subscribe((menuItemOptions: IMenuItemOption[]) => (this.menuItemOptionsSharedCollection = menuItemOptions));
  }
}
