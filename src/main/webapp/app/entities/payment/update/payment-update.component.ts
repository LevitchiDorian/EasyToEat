import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IRestaurantOrder } from 'app/entities/restaurant-order/restaurant-order.model';
import { RestaurantOrderService } from 'app/entities/restaurant-order/service/restaurant-order.service';
import { PaymentMethod } from 'app/entities/enumerations/payment-method.model';
import { PaymentStatus } from 'app/entities/enumerations/payment-status.model';
import { PaymentService } from '../service/payment.service';
import { IPayment } from '../payment.model';
import { PaymentFormGroup, PaymentFormService } from './payment-form.service';

@Component({
  selector: 'jhi-payment-update',
  templateUrl: './payment-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PaymentUpdateComponent implements OnInit {
  isSaving = false;
  payment: IPayment | null = null;
  paymentMethodValues = Object.keys(PaymentMethod);
  paymentStatusValues = Object.keys(PaymentStatus);

  usersSharedCollection: IUser[] = [];
  restaurantOrdersSharedCollection: IRestaurantOrder[] = [];

  protected paymentService = inject(PaymentService);
  protected paymentFormService = inject(PaymentFormService);
  protected userService = inject(UserService);
  protected restaurantOrderService = inject(RestaurantOrderService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PaymentFormGroup = this.paymentFormService.createPaymentFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareRestaurantOrder = (o1: IRestaurantOrder | null, o2: IRestaurantOrder | null): boolean =>
    this.restaurantOrderService.compareRestaurantOrder(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ payment }) => {
      this.payment = payment;
      if (payment) {
        this.updateForm(payment);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const payment = this.paymentFormService.getPayment(this.editForm);
    if (payment.id !== null) {
      this.subscribeToSaveResponse(this.paymentService.update(payment));
    } else {
      this.subscribeToSaveResponse(this.paymentService.create(payment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPayment>>): void {
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

  protected updateForm(payment: IPayment): void {
    this.payment = payment;
    this.paymentFormService.resetForm(this.editForm, payment);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, payment.processedBy);
    this.restaurantOrdersSharedCollection = this.restaurantOrderService.addRestaurantOrderToCollectionIfMissing<IRestaurantOrder>(
      this.restaurantOrdersSharedCollection,
      payment.order,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.payment?.processedBy)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.restaurantOrderService
      .query()
      .pipe(map((res: HttpResponse<IRestaurantOrder[]>) => res.body ?? []))
      .pipe(
        map((restaurantOrders: IRestaurantOrder[]) =>
          this.restaurantOrderService.addRestaurantOrderToCollectionIfMissing<IRestaurantOrder>(restaurantOrders, this.payment?.order),
        ),
      )
      .subscribe((restaurantOrders: IRestaurantOrder[]) => (this.restaurantOrdersSharedCollection = restaurantOrders));
  }
}
