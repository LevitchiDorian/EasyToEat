import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../order-item-option-selection.test-samples';

import { OrderItemOptionSelectionFormService } from './order-item-option-selection-form.service';

describe('OrderItemOptionSelection Form Service', () => {
  let service: OrderItemOptionSelectionFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OrderItemOptionSelectionFormService);
  });

  describe('Service methods', () => {
    describe('createOrderItemOptionSelectionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createOrderItemOptionSelectionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantity: expect.any(Object),
            unitPrice: expect.any(Object),
            optionValue: expect.any(Object),
            orderItem: expect.any(Object),
          }),
        );
      });

      it('passing IOrderItemOptionSelection should create a new form with FormGroup', () => {
        const formGroup = service.createOrderItemOptionSelectionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantity: expect.any(Object),
            unitPrice: expect.any(Object),
            optionValue: expect.any(Object),
            orderItem: expect.any(Object),
          }),
        );
      });
    });

    describe('getOrderItemOptionSelection', () => {
      it('should return NewOrderItemOptionSelection for default OrderItemOptionSelection initial value', () => {
        const formGroup = service.createOrderItemOptionSelectionFormGroup(sampleWithNewData);

        const orderItemOptionSelection = service.getOrderItemOptionSelection(formGroup) as any;

        expect(orderItemOptionSelection).toMatchObject(sampleWithNewData);
      });

      it('should return NewOrderItemOptionSelection for empty OrderItemOptionSelection initial value', () => {
        const formGroup = service.createOrderItemOptionSelectionFormGroup();

        const orderItemOptionSelection = service.getOrderItemOptionSelection(formGroup) as any;

        expect(orderItemOptionSelection).toMatchObject({});
      });

      it('should return IOrderItemOptionSelection', () => {
        const formGroup = service.createOrderItemOptionSelectionFormGroup(sampleWithRequiredData);

        const orderItemOptionSelection = service.getOrderItemOptionSelection(formGroup) as any;

        expect(orderItemOptionSelection).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IOrderItemOptionSelection should not enable id FormControl', () => {
        const formGroup = service.createOrderItemOptionSelectionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewOrderItemOptionSelection should disable id FormControl', () => {
        const formGroup = service.createOrderItemOptionSelectionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
