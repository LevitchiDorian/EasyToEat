import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../restaurant-table.test-samples';

import { RestaurantTableFormService } from './restaurant-table-form.service';

describe('RestaurantTable Form Service', () => {
  let service: RestaurantTableFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RestaurantTableFormService);
  });

  describe('Service methods', () => {
    describe('createRestaurantTableFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRestaurantTableFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            tableNumber: expect.any(Object),
            shape: expect.any(Object),
            minCapacity: expect.any(Object),
            maxCapacity: expect.any(Object),
            positionX: expect.any(Object),
            positionY: expect.any(Object),
            widthPx: expect.any(Object),
            heightPx: expect.any(Object),
            rotation: expect.any(Object),
            status: expect.any(Object),
            isActive: expect.any(Object),
            notes: expect.any(Object),
            room: expect.any(Object),
          }),
        );
      });

      it('passing IRestaurantTable should create a new form with FormGroup', () => {
        const formGroup = service.createRestaurantTableFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            tableNumber: expect.any(Object),
            shape: expect.any(Object),
            minCapacity: expect.any(Object),
            maxCapacity: expect.any(Object),
            positionX: expect.any(Object),
            positionY: expect.any(Object),
            widthPx: expect.any(Object),
            heightPx: expect.any(Object),
            rotation: expect.any(Object),
            status: expect.any(Object),
            isActive: expect.any(Object),
            notes: expect.any(Object),
            room: expect.any(Object),
          }),
        );
      });
    });

    describe('getRestaurantTable', () => {
      it('should return NewRestaurantTable for default RestaurantTable initial value', () => {
        const formGroup = service.createRestaurantTableFormGroup(sampleWithNewData);

        const restaurantTable = service.getRestaurantTable(formGroup) as any;

        expect(restaurantTable).toMatchObject(sampleWithNewData);
      });

      it('should return NewRestaurantTable for empty RestaurantTable initial value', () => {
        const formGroup = service.createRestaurantTableFormGroup();

        const restaurantTable = service.getRestaurantTable(formGroup) as any;

        expect(restaurantTable).toMatchObject({});
      });

      it('should return IRestaurantTable', () => {
        const formGroup = service.createRestaurantTableFormGroup(sampleWithRequiredData);

        const restaurantTable = service.getRestaurantTable(formGroup) as any;

        expect(restaurantTable).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRestaurantTable should not enable id FormControl', () => {
        const formGroup = service.createRestaurantTableFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRestaurantTable should disable id FormControl', () => {
        const formGroup = service.createRestaurantTableFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
