import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IRestaurantTable } from 'app/entities/restaurant-table/restaurant-table.model';
import { RestaurantTableService } from 'app/entities/restaurant-table/service/restaurant-table.service';
import { IPromotion } from 'app/entities/promotion/promotion.model';
import { PromotionService } from 'app/entities/promotion/service/promotion.service';
import { IReservation } from 'app/entities/reservation/reservation.model';
import { ReservationService } from 'app/entities/reservation/service/reservation.service';
import { IRestaurantOrder } from '../restaurant-order.model';
import { RestaurantOrderService } from '../service/restaurant-order.service';
import { RestaurantOrderFormService } from './restaurant-order-form.service';

import { RestaurantOrderUpdateComponent } from './restaurant-order-update.component';

describe('RestaurantOrder Management Update Component', () => {
  let comp: RestaurantOrderUpdateComponent;
  let fixture: ComponentFixture<RestaurantOrderUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let restaurantOrderFormService: RestaurantOrderFormService;
  let restaurantOrderService: RestaurantOrderService;
  let locationService: LocationService;
  let userService: UserService;
  let restaurantTableService: RestaurantTableService;
  let promotionService: PromotionService;
  let reservationService: ReservationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RestaurantOrderUpdateComponent],
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
      .overrideTemplate(RestaurantOrderUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RestaurantOrderUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    restaurantOrderFormService = TestBed.inject(RestaurantOrderFormService);
    restaurantOrderService = TestBed.inject(RestaurantOrderService);
    locationService = TestBed.inject(LocationService);
    userService = TestBed.inject(UserService);
    restaurantTableService = TestBed.inject(RestaurantTableService);
    promotionService = TestBed.inject(PromotionService);
    reservationService = TestBed.inject(ReservationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Location query and add missing value', () => {
      const restaurantOrder: IRestaurantOrder = { id: 25621 };
      const location: ILocation = { id: 8454 };
      restaurantOrder.location = location;

      const locationCollection: ILocation[] = [{ id: 8454 }];
      jest.spyOn(locationService, 'query').mockReturnValue(of(new HttpResponse({ body: locationCollection })));
      const additionalLocations = [location];
      const expectedCollection: ILocation[] = [...additionalLocations, ...locationCollection];
      jest.spyOn(locationService, 'addLocationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ restaurantOrder });
      comp.ngOnInit();

      expect(locationService.query).toHaveBeenCalled();
      expect(locationService.addLocationToCollectionIfMissing).toHaveBeenCalledWith(
        locationCollection,
        ...additionalLocations.map(expect.objectContaining),
      );
      expect(comp.locationsSharedCollection).toEqual(expectedCollection);
    });

    it('should call User query and add missing value', () => {
      const restaurantOrder: IRestaurantOrder = { id: 25621 };
      const client: IUser = { id: 3944 };
      restaurantOrder.client = client;
      const assignedWaiter: IUser = { id: 3944 };
      restaurantOrder.assignedWaiter = assignedWaiter;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [client, assignedWaiter];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ restaurantOrder });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('should call RestaurantTable query and add missing value', () => {
      const restaurantOrder: IRestaurantOrder = { id: 25621 };
      const table: IRestaurantTable = { id: 23892 };
      restaurantOrder.table = table;

      const restaurantTableCollection: IRestaurantTable[] = [{ id: 23892 }];
      jest.spyOn(restaurantTableService, 'query').mockReturnValue(of(new HttpResponse({ body: restaurantTableCollection })));
      const additionalRestaurantTables = [table];
      const expectedCollection: IRestaurantTable[] = [...additionalRestaurantTables, ...restaurantTableCollection];
      jest.spyOn(restaurantTableService, 'addRestaurantTableToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ restaurantOrder });
      comp.ngOnInit();

      expect(restaurantTableService.query).toHaveBeenCalled();
      expect(restaurantTableService.addRestaurantTableToCollectionIfMissing).toHaveBeenCalledWith(
        restaurantTableCollection,
        ...additionalRestaurantTables.map(expect.objectContaining),
      );
      expect(comp.restaurantTablesSharedCollection).toEqual(expectedCollection);
    });

    it('should call Promotion query and add missing value', () => {
      const restaurantOrder: IRestaurantOrder = { id: 25621 };
      const promotion: IPromotion = { id: 30739 };
      restaurantOrder.promotion = promotion;

      const promotionCollection: IPromotion[] = [{ id: 30739 }];
      jest.spyOn(promotionService, 'query').mockReturnValue(of(new HttpResponse({ body: promotionCollection })));
      const additionalPromotions = [promotion];
      const expectedCollection: IPromotion[] = [...additionalPromotions, ...promotionCollection];
      jest.spyOn(promotionService, 'addPromotionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ restaurantOrder });
      comp.ngOnInit();

      expect(promotionService.query).toHaveBeenCalled();
      expect(promotionService.addPromotionToCollectionIfMissing).toHaveBeenCalledWith(
        promotionCollection,
        ...additionalPromotions.map(expect.objectContaining),
      );
      expect(comp.promotionsSharedCollection).toEqual(expectedCollection);
    });

    it('should call Reservation query and add missing value', () => {
      const restaurantOrder: IRestaurantOrder = { id: 25621 };
      const reservation: IReservation = { id: 27139 };
      restaurantOrder.reservation = reservation;

      const reservationCollection: IReservation[] = [{ id: 27139 }];
      jest.spyOn(reservationService, 'query').mockReturnValue(of(new HttpResponse({ body: reservationCollection })));
      const additionalReservations = [reservation];
      const expectedCollection: IReservation[] = [...additionalReservations, ...reservationCollection];
      jest.spyOn(reservationService, 'addReservationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ restaurantOrder });
      comp.ngOnInit();

      expect(reservationService.query).toHaveBeenCalled();
      expect(reservationService.addReservationToCollectionIfMissing).toHaveBeenCalledWith(
        reservationCollection,
        ...additionalReservations.map(expect.objectContaining),
      );
      expect(comp.reservationsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const restaurantOrder: IRestaurantOrder = { id: 25621 };
      const location: ILocation = { id: 8454 };
      restaurantOrder.location = location;
      const client: IUser = { id: 3944 };
      restaurantOrder.client = client;
      const assignedWaiter: IUser = { id: 3944 };
      restaurantOrder.assignedWaiter = assignedWaiter;
      const table: IRestaurantTable = { id: 23892 };
      restaurantOrder.table = table;
      const promotion: IPromotion = { id: 30739 };
      restaurantOrder.promotion = promotion;
      const reservation: IReservation = { id: 27139 };
      restaurantOrder.reservation = reservation;

      activatedRoute.data = of({ restaurantOrder });
      comp.ngOnInit();

      expect(comp.locationsSharedCollection).toContainEqual(location);
      expect(comp.usersSharedCollection).toContainEqual(client);
      expect(comp.usersSharedCollection).toContainEqual(assignedWaiter);
      expect(comp.restaurantTablesSharedCollection).toContainEqual(table);
      expect(comp.promotionsSharedCollection).toContainEqual(promotion);
      expect(comp.reservationsSharedCollection).toContainEqual(reservation);
      expect(comp.restaurantOrder).toEqual(restaurantOrder);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRestaurantOrder>>();
      const restaurantOrder = { id: 2705 };
      jest.spyOn(restaurantOrderFormService, 'getRestaurantOrder').mockReturnValue(restaurantOrder);
      jest.spyOn(restaurantOrderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ restaurantOrder });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: restaurantOrder }));
      saveSubject.complete();

      // THEN
      expect(restaurantOrderFormService.getRestaurantOrder).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(restaurantOrderService.update).toHaveBeenCalledWith(expect.objectContaining(restaurantOrder));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRestaurantOrder>>();
      const restaurantOrder = { id: 2705 };
      jest.spyOn(restaurantOrderFormService, 'getRestaurantOrder').mockReturnValue({ id: null });
      jest.spyOn(restaurantOrderService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ restaurantOrder: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: restaurantOrder }));
      saveSubject.complete();

      // THEN
      expect(restaurantOrderFormService.getRestaurantOrder).toHaveBeenCalled();
      expect(restaurantOrderService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRestaurantOrder>>();
      const restaurantOrder = { id: 2705 };
      jest.spyOn(restaurantOrderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ restaurantOrder });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(restaurantOrderService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareLocation', () => {
      it('should forward to locationService', () => {
        const entity = { id: 8454 };
        const entity2 = { id: 13013 };
        jest.spyOn(locationService, 'compareLocation');
        comp.compareLocation(entity, entity2);
        expect(locationService.compareLocation).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareRestaurantTable', () => {
      it('should forward to restaurantTableService', () => {
        const entity = { id: 23892 };
        const entity2 = { id: 31409 };
        jest.spyOn(restaurantTableService, 'compareRestaurantTable');
        comp.compareRestaurantTable(entity, entity2);
        expect(restaurantTableService.compareRestaurantTable).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('comparePromotion', () => {
      it('should forward to promotionService', () => {
        const entity = { id: 30739 };
        const entity2 = { id: 21560 };
        jest.spyOn(promotionService, 'comparePromotion');
        comp.comparePromotion(entity, entity2);
        expect(promotionService.comparePromotion).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareReservation', () => {
      it('should forward to reservationService', () => {
        const entity = { id: 27139 };
        const entity2 = { id: 21991 };
        jest.spyOn(reservationService, 'compareReservation');
        comp.compareReservation(entity, entity2);
        expect(reservationService.compareReservation).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
