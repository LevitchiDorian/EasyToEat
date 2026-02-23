import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IMenuItemOptionValue } from 'app/entities/menu-item-option-value/menu-item-option-value.model';
import { MenuItemOptionValueService } from 'app/entities/menu-item-option-value/service/menu-item-option-value.service';
import { IOrderItem } from 'app/entities/order-item/order-item.model';
import { OrderItemService } from 'app/entities/order-item/service/order-item.service';
import { IOrderItemOptionSelection } from '../order-item-option-selection.model';
import { OrderItemOptionSelectionService } from '../service/order-item-option-selection.service';
import { OrderItemOptionSelectionFormService } from './order-item-option-selection-form.service';

import { OrderItemOptionSelectionUpdateComponent } from './order-item-option-selection-update.component';

describe('OrderItemOptionSelection Management Update Component', () => {
  let comp: OrderItemOptionSelectionUpdateComponent;
  let fixture: ComponentFixture<OrderItemOptionSelectionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let orderItemOptionSelectionFormService: OrderItemOptionSelectionFormService;
  let orderItemOptionSelectionService: OrderItemOptionSelectionService;
  let menuItemOptionValueService: MenuItemOptionValueService;
  let orderItemService: OrderItemService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [OrderItemOptionSelectionUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(OrderItemOptionSelectionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrderItemOptionSelectionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    orderItemOptionSelectionFormService = TestBed.inject(OrderItemOptionSelectionFormService);
    orderItemOptionSelectionService = TestBed.inject(OrderItemOptionSelectionService);
    menuItemOptionValueService = TestBed.inject(MenuItemOptionValueService);
    orderItemService = TestBed.inject(OrderItemService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call MenuItemOptionValue query and add missing value', () => {
      const orderItemOptionSelection: IOrderItemOptionSelection = { id: 2555 };
      const optionValue: IMenuItemOptionValue = { id: 31564 };
      orderItemOptionSelection.optionValue = optionValue;

      const menuItemOptionValueCollection: IMenuItemOptionValue[] = [{ id: 31564 }];
      jest.spyOn(menuItemOptionValueService, 'query').mockReturnValue(of(new HttpResponse({ body: menuItemOptionValueCollection })));
      const additionalMenuItemOptionValues = [optionValue];
      const expectedCollection: IMenuItemOptionValue[] = [...additionalMenuItemOptionValues, ...menuItemOptionValueCollection];
      jest.spyOn(menuItemOptionValueService, 'addMenuItemOptionValueToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ orderItemOptionSelection });
      comp.ngOnInit();

      expect(menuItemOptionValueService.query).toHaveBeenCalled();
      expect(menuItemOptionValueService.addMenuItemOptionValueToCollectionIfMissing).toHaveBeenCalledWith(
        menuItemOptionValueCollection,
        ...additionalMenuItemOptionValues.map(expect.objectContaining),
      );
      expect(comp.menuItemOptionValuesSharedCollection).toEqual(expectedCollection);
    });

    it('should call OrderItem query and add missing value', () => {
      const orderItemOptionSelection: IOrderItemOptionSelection = { id: 2555 };
      const orderItem: IOrderItem = { id: 25971 };
      orderItemOptionSelection.orderItem = orderItem;

      const orderItemCollection: IOrderItem[] = [{ id: 25971 }];
      jest.spyOn(orderItemService, 'query').mockReturnValue(of(new HttpResponse({ body: orderItemCollection })));
      const additionalOrderItems = [orderItem];
      const expectedCollection: IOrderItem[] = [...additionalOrderItems, ...orderItemCollection];
      jest.spyOn(orderItemService, 'addOrderItemToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ orderItemOptionSelection });
      comp.ngOnInit();

      expect(orderItemService.query).toHaveBeenCalled();
      expect(orderItemService.addOrderItemToCollectionIfMissing).toHaveBeenCalledWith(
        orderItemCollection,
        ...additionalOrderItems.map(expect.objectContaining),
      );
      expect(comp.orderItemsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const orderItemOptionSelection: IOrderItemOptionSelection = { id: 2555 };
      const optionValue: IMenuItemOptionValue = { id: 31564 };
      orderItemOptionSelection.optionValue = optionValue;
      const orderItem: IOrderItem = { id: 25971 };
      orderItemOptionSelection.orderItem = orderItem;

      activatedRoute.data = of({ orderItemOptionSelection });
      comp.ngOnInit();

      expect(comp.menuItemOptionValuesSharedCollection).toContainEqual(optionValue);
      expect(comp.orderItemsSharedCollection).toContainEqual(orderItem);
      expect(comp.orderItemOptionSelection).toEqual(orderItemOptionSelection);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrderItemOptionSelection>>();
      const orderItemOptionSelection = { id: 17155 };
      jest.spyOn(orderItemOptionSelectionFormService, 'getOrderItemOptionSelection').mockReturnValue(orderItemOptionSelection);
      jest.spyOn(orderItemOptionSelectionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderItemOptionSelection });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orderItemOptionSelection }));
      saveSubject.complete();

      // THEN
      expect(orderItemOptionSelectionFormService.getOrderItemOptionSelection).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(orderItemOptionSelectionService.update).toHaveBeenCalledWith(expect.objectContaining(orderItemOptionSelection));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrderItemOptionSelection>>();
      const orderItemOptionSelection = { id: 17155 };
      jest.spyOn(orderItemOptionSelectionFormService, 'getOrderItemOptionSelection').mockReturnValue({ id: null });
      jest.spyOn(orderItemOptionSelectionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderItemOptionSelection: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orderItemOptionSelection }));
      saveSubject.complete();

      // THEN
      expect(orderItemOptionSelectionFormService.getOrderItemOptionSelection).toHaveBeenCalled();
      expect(orderItemOptionSelectionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrderItemOptionSelection>>();
      const orderItemOptionSelection = { id: 17155 };
      jest.spyOn(orderItemOptionSelectionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderItemOptionSelection });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(orderItemOptionSelectionService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareMenuItemOptionValue', () => {
      it('should forward to menuItemOptionValueService', () => {
        const entity = { id: 31564 };
        const entity2 = { id: 30184 };
        jest.spyOn(menuItemOptionValueService, 'compareMenuItemOptionValue');
        comp.compareMenuItemOptionValue(entity, entity2);
        expect(menuItemOptionValueService.compareMenuItemOptionValue).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareOrderItem', () => {
      it('should forward to orderItemService', () => {
        const entity = { id: 25971 };
        const entity2 = { id: 123 };
        jest.spyOn(orderItemService, 'compareOrderItem');
        comp.compareOrderItem(entity, entity2);
        expect(orderItemService.compareOrderItem).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
