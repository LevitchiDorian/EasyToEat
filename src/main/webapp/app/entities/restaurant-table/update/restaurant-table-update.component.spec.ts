import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IDiningRoom } from 'app/entities/dining-room/dining-room.model';
import { DiningRoomService } from 'app/entities/dining-room/service/dining-room.service';
import { RestaurantTableService } from '../service/restaurant-table.service';
import { IRestaurantTable } from '../restaurant-table.model';
import { RestaurantTableFormService } from './restaurant-table-form.service';

import { RestaurantTableUpdateComponent } from './restaurant-table-update.component';

describe('RestaurantTable Management Update Component', () => {
  let comp: RestaurantTableUpdateComponent;
  let fixture: ComponentFixture<RestaurantTableUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let restaurantTableFormService: RestaurantTableFormService;
  let restaurantTableService: RestaurantTableService;
  let diningRoomService: DiningRoomService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RestaurantTableUpdateComponent],
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
      .overrideTemplate(RestaurantTableUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RestaurantTableUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    restaurantTableFormService = TestBed.inject(RestaurantTableFormService);
    restaurantTableService = TestBed.inject(RestaurantTableService);
    diningRoomService = TestBed.inject(DiningRoomService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call DiningRoom query and add missing value', () => {
      const restaurantTable: IRestaurantTable = { id: 31409 };
      const room: IDiningRoom = { id: 3993 };
      restaurantTable.room = room;

      const diningRoomCollection: IDiningRoom[] = [{ id: 3993 }];
      jest.spyOn(diningRoomService, 'query').mockReturnValue(of(new HttpResponse({ body: diningRoomCollection })));
      const additionalDiningRooms = [room];
      const expectedCollection: IDiningRoom[] = [...additionalDiningRooms, ...diningRoomCollection];
      jest.spyOn(diningRoomService, 'addDiningRoomToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ restaurantTable });
      comp.ngOnInit();

      expect(diningRoomService.query).toHaveBeenCalled();
      expect(diningRoomService.addDiningRoomToCollectionIfMissing).toHaveBeenCalledWith(
        diningRoomCollection,
        ...additionalDiningRooms.map(expect.objectContaining),
      );
      expect(comp.diningRoomsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const restaurantTable: IRestaurantTable = { id: 31409 };
      const room: IDiningRoom = { id: 3993 };
      restaurantTable.room = room;

      activatedRoute.data = of({ restaurantTable });
      comp.ngOnInit();

      expect(comp.diningRoomsSharedCollection).toContainEqual(room);
      expect(comp.restaurantTable).toEqual(restaurantTable);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRestaurantTable>>();
      const restaurantTable = { id: 23892 };
      jest.spyOn(restaurantTableFormService, 'getRestaurantTable').mockReturnValue(restaurantTable);
      jest.spyOn(restaurantTableService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ restaurantTable });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: restaurantTable }));
      saveSubject.complete();

      // THEN
      expect(restaurantTableFormService.getRestaurantTable).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(restaurantTableService.update).toHaveBeenCalledWith(expect.objectContaining(restaurantTable));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRestaurantTable>>();
      const restaurantTable = { id: 23892 };
      jest.spyOn(restaurantTableFormService, 'getRestaurantTable').mockReturnValue({ id: null });
      jest.spyOn(restaurantTableService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ restaurantTable: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: restaurantTable }));
      saveSubject.complete();

      // THEN
      expect(restaurantTableFormService.getRestaurantTable).toHaveBeenCalled();
      expect(restaurantTableService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRestaurantTable>>();
      const restaurantTable = { id: 23892 };
      jest.spyOn(restaurantTableService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ restaurantTable });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(restaurantTableService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareDiningRoom', () => {
      it('should forward to diningRoomService', () => {
        const entity = { id: 3993 };
        const entity2 = { id: 12477 };
        jest.spyOn(diningRoomService, 'compareDiningRoom');
        comp.compareDiningRoom(entity, entity2);
        expect(diningRoomService.compareDiningRoom).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
