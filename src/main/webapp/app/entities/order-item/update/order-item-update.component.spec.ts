import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IMenuItem } from 'app/entities/menu-item/menu-item.model';
import { MenuItemService } from 'app/entities/menu-item/service/menu-item.service';
import { IRestaurantOrder } from 'app/entities/restaurant-order/restaurant-order.model';
import { RestaurantOrderService } from 'app/entities/restaurant-order/service/restaurant-order.service';
import { IOrderItem } from '../order-item.model';
import { OrderItemService } from '../service/order-item.service';
import { OrderItemFormService } from './order-item-form.service';

import { OrderItemUpdateComponent } from './order-item-update.component';

describe('OrderItem Management Update Component', () => {
  let comp: OrderItemUpdateComponent;
  let fixture: ComponentFixture<OrderItemUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let orderItemFormService: OrderItemFormService;
  let orderItemService: OrderItemService;
  let menuItemService: MenuItemService;
  let restaurantOrderService: RestaurantOrderService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [OrderItemUpdateComponent],
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
      .overrideTemplate(OrderItemUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrderItemUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    orderItemFormService = TestBed.inject(OrderItemFormService);
    orderItemService = TestBed.inject(OrderItemService);
    menuItemService = TestBed.inject(MenuItemService);
    restaurantOrderService = TestBed.inject(RestaurantOrderService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call MenuItem query and add missing value', () => {
      const orderItem: IOrderItem = { id: 123 };
      const menuItem: IMenuItem = { id: 11248 };
      orderItem.menuItem = menuItem;

      const menuItemCollection: IMenuItem[] = [{ id: 11248 }];
      jest.spyOn(menuItemService, 'query').mockReturnValue(of(new HttpResponse({ body: menuItemCollection })));
      const additionalMenuItems = [menuItem];
      const expectedCollection: IMenuItem[] = [...additionalMenuItems, ...menuItemCollection];
      jest.spyOn(menuItemService, 'addMenuItemToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ orderItem });
      comp.ngOnInit();

      expect(menuItemService.query).toHaveBeenCalled();
      expect(menuItemService.addMenuItemToCollectionIfMissing).toHaveBeenCalledWith(
        menuItemCollection,
        ...additionalMenuItems.map(expect.objectContaining),
      );
      expect(comp.menuItemsSharedCollection).toEqual(expectedCollection);
    });

    it('should call RestaurantOrder query and add missing value', () => {
      const orderItem: IOrderItem = { id: 123 };
      const order: IRestaurantOrder = { id: 2705 };
      orderItem.order = order;

      const restaurantOrderCollection: IRestaurantOrder[] = [{ id: 2705 }];
      jest.spyOn(restaurantOrderService, 'query').mockReturnValue(of(new HttpResponse({ body: restaurantOrderCollection })));
      const additionalRestaurantOrders = [order];
      const expectedCollection: IRestaurantOrder[] = [...additionalRestaurantOrders, ...restaurantOrderCollection];
      jest.spyOn(restaurantOrderService, 'addRestaurantOrderToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ orderItem });
      comp.ngOnInit();

      expect(restaurantOrderService.query).toHaveBeenCalled();
      expect(restaurantOrderService.addRestaurantOrderToCollectionIfMissing).toHaveBeenCalledWith(
        restaurantOrderCollection,
        ...additionalRestaurantOrders.map(expect.objectContaining),
      );
      expect(comp.restaurantOrdersSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const orderItem: IOrderItem = { id: 123 };
      const menuItem: IMenuItem = { id: 11248 };
      orderItem.menuItem = menuItem;
      const order: IRestaurantOrder = { id: 2705 };
      orderItem.order = order;

      activatedRoute.data = of({ orderItem });
      comp.ngOnInit();

      expect(comp.menuItemsSharedCollection).toContainEqual(menuItem);
      expect(comp.restaurantOrdersSharedCollection).toContainEqual(order);
      expect(comp.orderItem).toEqual(orderItem);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrderItem>>();
      const orderItem = { id: 25971 };
      jest.spyOn(orderItemFormService, 'getOrderItem').mockReturnValue(orderItem);
      jest.spyOn(orderItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orderItem }));
      saveSubject.complete();

      // THEN
      expect(orderItemFormService.getOrderItem).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(orderItemService.update).toHaveBeenCalledWith(expect.objectContaining(orderItem));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrderItem>>();
      const orderItem = { id: 25971 };
      jest.spyOn(orderItemFormService, 'getOrderItem').mockReturnValue({ id: null });
      jest.spyOn(orderItemService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderItem: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orderItem }));
      saveSubject.complete();

      // THEN
      expect(orderItemFormService.getOrderItem).toHaveBeenCalled();
      expect(orderItemService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrderItem>>();
      const orderItem = { id: 25971 };
      jest.spyOn(orderItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(orderItemService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareMenuItem', () => {
      it('should forward to menuItemService', () => {
        const entity = { id: 11248 };
        const entity2 = { id: 12993 };
        jest.spyOn(menuItemService, 'compareMenuItem');
        comp.compareMenuItem(entity, entity2);
        expect(menuItemService.compareMenuItem).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareRestaurantOrder', () => {
      it('should forward to restaurantOrderService', () => {
        const entity = { id: 2705 };
        const entity2 = { id: 25621 };
        jest.spyOn(restaurantOrderService, 'compareRestaurantOrder');
        comp.compareRestaurantOrder(entity, entity2);
        expect(restaurantOrderService.compareRestaurantOrder).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
