import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IRestaurantTable } from 'app/entities/restaurant-table/restaurant-table.model';
import { RestaurantTableService } from 'app/entities/restaurant-table/service/restaurant-table.service';
import { IReservation } from 'app/entities/reservation/reservation.model';
import { ReservationService } from 'app/entities/reservation/service/reservation.service';
import { IReservationTable } from '../reservation-table.model';
import { ReservationTableService } from '../service/reservation-table.service';
import { ReservationTableFormService } from './reservation-table-form.service';

import { ReservationTableUpdateComponent } from './reservation-table-update.component';

describe('ReservationTable Management Update Component', () => {
  let comp: ReservationTableUpdateComponent;
  let fixture: ComponentFixture<ReservationTableUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let reservationTableFormService: ReservationTableFormService;
  let reservationTableService: ReservationTableService;
  let restaurantTableService: RestaurantTableService;
  let reservationService: ReservationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReservationTableUpdateComponent],
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
      .overrideTemplate(ReservationTableUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ReservationTableUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    reservationTableFormService = TestBed.inject(ReservationTableFormService);
    reservationTableService = TestBed.inject(ReservationTableService);
    restaurantTableService = TestBed.inject(RestaurantTableService);
    reservationService = TestBed.inject(ReservationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call RestaurantTable query and add missing value', () => {
      const reservationTable: IReservationTable = { id: 25191 };
      const table: IRestaurantTable = { id: 23892 };
      reservationTable.table = table;

      const restaurantTableCollection: IRestaurantTable[] = [{ id: 23892 }];
      jest.spyOn(restaurantTableService, 'query').mockReturnValue(of(new HttpResponse({ body: restaurantTableCollection })));
      const additionalRestaurantTables = [table];
      const expectedCollection: IRestaurantTable[] = [...additionalRestaurantTables, ...restaurantTableCollection];
      jest.spyOn(restaurantTableService, 'addRestaurantTableToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ reservationTable });
      comp.ngOnInit();

      expect(restaurantTableService.query).toHaveBeenCalled();
      expect(restaurantTableService.addRestaurantTableToCollectionIfMissing).toHaveBeenCalledWith(
        restaurantTableCollection,
        ...additionalRestaurantTables.map(expect.objectContaining),
      );
      expect(comp.restaurantTablesSharedCollection).toEqual(expectedCollection);
    });

    it('should call Reservation query and add missing value', () => {
      const reservationTable: IReservationTable = { id: 25191 };
      const reservation: IReservation = { id: 27139 };
      reservationTable.reservation = reservation;

      const reservationCollection: IReservation[] = [{ id: 27139 }];
      jest.spyOn(reservationService, 'query').mockReturnValue(of(new HttpResponse({ body: reservationCollection })));
      const additionalReservations = [reservation];
      const expectedCollection: IReservation[] = [...additionalReservations, ...reservationCollection];
      jest.spyOn(reservationService, 'addReservationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ reservationTable });
      comp.ngOnInit();

      expect(reservationService.query).toHaveBeenCalled();
      expect(reservationService.addReservationToCollectionIfMissing).toHaveBeenCalledWith(
        reservationCollection,
        ...additionalReservations.map(expect.objectContaining),
      );
      expect(comp.reservationsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const reservationTable: IReservationTable = { id: 25191 };
      const table: IRestaurantTable = { id: 23892 };
      reservationTable.table = table;
      const reservation: IReservation = { id: 27139 };
      reservationTable.reservation = reservation;

      activatedRoute.data = of({ reservationTable });
      comp.ngOnInit();

      expect(comp.restaurantTablesSharedCollection).toContainEqual(table);
      expect(comp.reservationsSharedCollection).toContainEqual(reservation);
      expect(comp.reservationTable).toEqual(reservationTable);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReservationTable>>();
      const reservationTable = { id: 202 };
      jest.spyOn(reservationTableFormService, 'getReservationTable').mockReturnValue(reservationTable);
      jest.spyOn(reservationTableService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reservationTable });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: reservationTable }));
      saveSubject.complete();

      // THEN
      expect(reservationTableFormService.getReservationTable).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(reservationTableService.update).toHaveBeenCalledWith(expect.objectContaining(reservationTable));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReservationTable>>();
      const reservationTable = { id: 202 };
      jest.spyOn(reservationTableFormService, 'getReservationTable').mockReturnValue({ id: null });
      jest.spyOn(reservationTableService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reservationTable: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: reservationTable }));
      saveSubject.complete();

      // THEN
      expect(reservationTableFormService.getReservationTable).toHaveBeenCalled();
      expect(reservationTableService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReservationTable>>();
      const reservationTable = { id: 202 };
      jest.spyOn(reservationTableService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reservationTable });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(reservationTableService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareRestaurantTable', () => {
      it('should forward to restaurantTableService', () => {
        const entity = { id: 23892 };
        const entity2 = { id: 31409 };
        jest.spyOn(restaurantTableService, 'compareRestaurantTable');
        comp.compareRestaurantTable(entity, entity2);
        expect(restaurantTableService.compareRestaurantTable).toHaveBeenCalledWith(entity, entity2);
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
