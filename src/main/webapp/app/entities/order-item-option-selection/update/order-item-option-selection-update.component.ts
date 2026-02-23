import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IMenuItemOptionValue } from 'app/entities/menu-item-option-value/menu-item-option-value.model';
import { MenuItemOptionValueService } from 'app/entities/menu-item-option-value/service/menu-item-option-value.service';
import { IOrderItem } from 'app/entities/order-item/order-item.model';
import { OrderItemService } from 'app/entities/order-item/service/order-item.service';
import { OrderItemOptionSelectionService } from '../service/order-item-option-selection.service';
import { IOrderItemOptionSelection } from '../order-item-option-selection.model';
import { OrderItemOptionSelectionFormGroup, OrderItemOptionSelectionFormService } from './order-item-option-selection-form.service';

@Component({
  selector: 'jhi-order-item-option-selection-update',
  templateUrl: './order-item-option-selection-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class OrderItemOptionSelectionUpdateComponent implements OnInit {
  isSaving = false;
  orderItemOptionSelection: IOrderItemOptionSelection | null = null;

  menuItemOptionValuesSharedCollection: IMenuItemOptionValue[] = [];
  orderItemsSharedCollection: IOrderItem[] = [];

  protected orderItemOptionSelectionService = inject(OrderItemOptionSelectionService);
  protected orderItemOptionSelectionFormService = inject(OrderItemOptionSelectionFormService);
  protected menuItemOptionValueService = inject(MenuItemOptionValueService);
  protected orderItemService = inject(OrderItemService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: OrderItemOptionSelectionFormGroup = this.orderItemOptionSelectionFormService.createOrderItemOptionSelectionFormGroup();

  compareMenuItemOptionValue = (o1: IMenuItemOptionValue | null, o2: IMenuItemOptionValue | null): boolean =>
    this.menuItemOptionValueService.compareMenuItemOptionValue(o1, o2);

  compareOrderItem = (o1: IOrderItem | null, o2: IOrderItem | null): boolean => this.orderItemService.compareOrderItem(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ orderItemOptionSelection }) => {
      this.orderItemOptionSelection = orderItemOptionSelection;
      if (orderItemOptionSelection) {
        this.updateForm(orderItemOptionSelection);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const orderItemOptionSelection = this.orderItemOptionSelectionFormService.getOrderItemOptionSelection(this.editForm);
    if (orderItemOptionSelection.id !== null) {
      this.subscribeToSaveResponse(this.orderItemOptionSelectionService.update(orderItemOptionSelection));
    } else {
      this.subscribeToSaveResponse(this.orderItemOptionSelectionService.create(orderItemOptionSelection));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOrderItemOptionSelection>>): void {
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

  protected updateForm(orderItemOptionSelection: IOrderItemOptionSelection): void {
    this.orderItemOptionSelection = orderItemOptionSelection;
    this.orderItemOptionSelectionFormService.resetForm(this.editForm, orderItemOptionSelection);

    this.menuItemOptionValuesSharedCollection =
      this.menuItemOptionValueService.addMenuItemOptionValueToCollectionIfMissing<IMenuItemOptionValue>(
        this.menuItemOptionValuesSharedCollection,
        orderItemOptionSelection.optionValue,
      );
    this.orderItemsSharedCollection = this.orderItemService.addOrderItemToCollectionIfMissing<IOrderItem>(
      this.orderItemsSharedCollection,
      orderItemOptionSelection.orderItem,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.menuItemOptionValueService
      .query()
      .pipe(map((res: HttpResponse<IMenuItemOptionValue[]>) => res.body ?? []))
      .pipe(
        map((menuItemOptionValues: IMenuItemOptionValue[]) =>
          this.menuItemOptionValueService.addMenuItemOptionValueToCollectionIfMissing<IMenuItemOptionValue>(
            menuItemOptionValues,
            this.orderItemOptionSelection?.optionValue,
          ),
        ),
      )
      .subscribe((menuItemOptionValues: IMenuItemOptionValue[]) => (this.menuItemOptionValuesSharedCollection = menuItemOptionValues));

    this.orderItemService
      .query()
      .pipe(map((res: HttpResponse<IOrderItem[]>) => res.body ?? []))
      .pipe(
        map((orderItems: IOrderItem[]) =>
          this.orderItemService.addOrderItemToCollectionIfMissing<IOrderItem>(orderItems, this.orderItemOptionSelection?.orderItem),
        ),
      )
      .subscribe((orderItems: IOrderItem[]) => (this.orderItemsSharedCollection = orderItems));
  }
}
