import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../restaurant-order.test-samples';

import { RestaurantOrderFormService } from './restaurant-order-form.service';

describe('RestaurantOrder Form Service', () => {
  let service: RestaurantOrderFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RestaurantOrderFormService);
  });

  describe('Service methods', () => {
    describe('createRestaurantOrderFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRestaurantOrderFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            orderCode: expect.any(Object),
            status: expect.any(Object),
            isPreOrder: expect.any(Object),
            scheduledFor: expect.any(Object),
            subtotal: expect.any(Object),
            discountAmount: expect.any(Object),
            taxAmount: expect.any(Object),
            totalAmount: expect.any(Object),
            specialInstructions: expect.any(Object),
            estimatedReadyTime: expect.any(Object),
            confirmedAt: expect.any(Object),
            completedAt: expect.any(Object),
            createdAt: expect.any(Object),
            updatedAt: expect.any(Object),
            location: expect.any(Object),
            client: expect.any(Object),
            assignedWaiter: expect.any(Object),
            table: expect.any(Object),
            promotion: expect.any(Object),
            reservation: expect.any(Object),
          }),
        );
      });

      it('passing IRestaurantOrder should create a new form with FormGroup', () => {
        const formGroup = service.createRestaurantOrderFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            orderCode: expect.any(Object),
            status: expect.any(Object),
            isPreOrder: expect.any(Object),
            scheduledFor: expect.any(Object),
            subtotal: expect.any(Object),
            discountAmount: expect.any(Object),
            taxAmount: expect.any(Object),
            totalAmount: expect.any(Object),
            specialInstructions: expect.any(Object),
            estimatedReadyTime: expect.any(Object),
            confirmedAt: expect.any(Object),
            completedAt: expect.any(Object),
            createdAt: expect.any(Object),
            updatedAt: expect.any(Object),
            location: expect.any(Object),
            client: expect.any(Object),
            assignedWaiter: expect.any(Object),
            table: expect.any(Object),
            promotion: expect.any(Object),
            reservation: expect.any(Object),
          }),
        );
      });
    });

    describe('getRestaurantOrder', () => {
      it('should return NewRestaurantOrder for default RestaurantOrder initial value', () => {
        const formGroup = service.createRestaurantOrderFormGroup(sampleWithNewData);

        const restaurantOrder = service.getRestaurantOrder(formGroup) as any;

        expect(restaurantOrder).toMatchObject(sampleWithNewData);
      });

      it('should return NewRestaurantOrder for empty RestaurantOrder initial value', () => {
        const formGroup = service.createRestaurantOrderFormGroup();

        const restaurantOrder = service.getRestaurantOrder(formGroup) as any;

        expect(restaurantOrder).toMatchObject({});
      });

      it('should return IRestaurantOrder', () => {
        const formGroup = service.createRestaurantOrderFormGroup(sampleWithRequiredData);

        const restaurantOrder = service.getRestaurantOrder(formGroup) as any;

        expect(restaurantOrder).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRestaurantOrder should not enable id FormControl', () => {
        const formGroup = service.createRestaurantOrderFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRestaurantOrder should disable id FormControl', () => {
        const formGroup = service.createRestaurantOrderFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
