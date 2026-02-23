import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IMenuItem } from 'app/entities/menu-item/menu-item.model';
import { MenuItemService } from 'app/entities/menu-item/service/menu-item.service';
import { IRestaurantOrder } from 'app/entities/restaurant-order/restaurant-order.model';
import { RestaurantOrderService } from 'app/entities/restaurant-order/service/restaurant-order.service';
import { OrderItemStatus } from 'app/entities/enumerations/order-item-status.model';
import { OrderItemService } from '../service/order-item.service';
import { IOrderItem } from '../order-item.model';
import { OrderItemFormGroup, OrderItemFormService } from './order-item-form.service';

@Component({
  selector: 'jhi-order-item-update',
  templateUrl: './order-item-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class OrderItemUpdateComponent implements OnInit {
  isSaving = false;
  orderItem: IOrderItem | null = null;
  orderItemStatusValues = Object.keys(OrderItemStatus);

  menuItemsSharedCollection: IMenuItem[] = [];
  restaurantOrdersSharedCollection: IRestaurantOrder[] = [];

  protected orderItemService = inject(OrderItemService);
  protected orderItemFormService = inject(OrderItemFormService);
  protected menuItemService = inject(MenuItemService);
  protected restaurantOrderService = inject(RestaurantOrderService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: OrderItemFormGroup = this.orderItemFormService.createOrderItemFormGroup();

  compareMenuItem = (o1: IMenuItem | null, o2: IMenuItem | null): boolean => this.menuItemService.compareMenuItem(o1, o2);

  compareRestaurantOrder = (o1: IRestaurantOrder | null, o2: IRestaurantOrder | null): boolean =>
    this.restaurantOrderService.compareRestaurantOrder(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ orderItem }) => {
      this.orderItem = orderItem;
      if (orderItem) {
        this.updateForm(orderItem);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const orderItem = this.orderItemFormService.getOrderItem(this.editForm);
    if (orderItem.id !== null) {
      this.subscribeToSaveResponse(this.orderItemService.update(orderItem));
    } else {
      this.subscribeToSaveResponse(this.orderItemService.create(orderItem));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOrderItem>>): void {
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

  protected updateForm(orderItem: IOrderItem): void {
    this.orderItem = orderItem;
    this.orderItemFormService.resetForm(this.editForm, orderItem);

    this.menuItemsSharedCollection = this.menuItemService.addMenuItemToCollectionIfMissing<IMenuItem>(
      this.menuItemsSharedCollection,
      orderItem.menuItem,
    );
    this.restaurantOrdersSharedCollection = this.restaurantOrderService.addRestaurantOrderToCollectionIfMissing<IRestaurantOrder>(
      this.restaurantOrdersSharedCollection,
      orderItem.order,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.menuItemService
      .query()
      .pipe(map((res: HttpResponse<IMenuItem[]>) => res.body ?? []))
      .pipe(
        map((menuItems: IMenuItem[]) =>
          this.menuItemService.addMenuItemToCollectionIfMissing<IMenuItem>(menuItems, this.orderItem?.menuItem),
        ),
      )
      .subscribe((menuItems: IMenuItem[]) => (this.menuItemsSharedCollection = menuItems));

    this.restaurantOrderService
      .query()
      .pipe(map((res: HttpResponse<IRestaurantOrder[]>) => res.body ?? []))
      .pipe(
        map((restaurantOrders: IRestaurantOrder[]) =>
          this.restaurantOrderService.addRestaurantOrderToCollectionIfMissing<IRestaurantOrder>(restaurantOrders, this.orderItem?.order),
        ),
      )
      .subscribe((restaurantOrders: IRestaurantOrder[]) => (this.restaurantOrdersSharedCollection = restaurantOrders));
  }
}
