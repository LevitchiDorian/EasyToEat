import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';
import { DiningRoomService } from '../service/dining-room.service';
import { IDiningRoom } from '../dining-room.model';
import { DiningRoomFormService } from './dining-room-form.service';

import { DiningRoomUpdateComponent } from './dining-room-update.component';

describe('DiningRoom Management Update Component', () => {
  let comp: DiningRoomUpdateComponent;
  let fixture: ComponentFixture<DiningRoomUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let diningRoomFormService: DiningRoomFormService;
  let diningRoomService: DiningRoomService;
  let locationService: LocationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DiningRoomUpdateComponent],
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
      .overrideTemplate(DiningRoomUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DiningRoomUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    diningRoomFormService = TestBed.inject(DiningRoomFormService);
    diningRoomService = TestBed.inject(DiningRoomService);
    locationService = TestBed.inject(LocationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Location query and add missing value', () => {
      const diningRoom: IDiningRoom = { id: 12477 };
      const location: ILocation = { id: 8454 };
      diningRoom.location = location;

      const locationCollection: ILocation[] = [{ id: 8454 }];
      jest.spyOn(locationService, 'query').mockReturnValue(of(new HttpResponse({ body: locationCollection })));
      const additionalLocations = [location];
      const expectedCollection: ILocation[] = [...additionalLocations, ...locationCollection];
      jest.spyOn(locationService, 'addLocationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ diningRoom });
      comp.ngOnInit();

      expect(locationService.query).toHaveBeenCalled();
      expect(locationService.addLocationToCollectionIfMissing).toHaveBeenCalledWith(
        locationCollection,
        ...additionalLocations.map(expect.objectContaining),
      );
      expect(comp.locationsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const diningRoom: IDiningRoom = { id: 12477 };
      const location: ILocation = { id: 8454 };
      diningRoom.location = location;

      activatedRoute.data = of({ diningRoom });
      comp.ngOnInit();

      expect(comp.locationsSharedCollection).toContainEqual(location);
      expect(comp.diningRoom).toEqual(diningRoom);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDiningRoom>>();
      const diningRoom = { id: 3993 };
      jest.spyOn(diningRoomFormService, 'getDiningRoom').mockReturnValue(diningRoom);
      jest.spyOn(diningRoomService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ diningRoom });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: diningRoom }));
      saveSubject.complete();

      // THEN
      expect(diningRoomFormService.getDiningRoom).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(diningRoomService.update).toHaveBeenCalledWith(expect.objectContaining(diningRoom));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDiningRoom>>();
      const diningRoom = { id: 3993 };
      jest.spyOn(diningRoomFormService, 'getDiningRoom').mockReturnValue({ id: null });
      jest.spyOn(diningRoomService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ diningRoom: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: diningRoom }));
      saveSubject.complete();

      // THEN
      expect(diningRoomFormService.getDiningRoom).toHaveBeenCalled();
      expect(diningRoomService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDiningRoom>>();
      const diningRoom = { id: 3993 };
      jest.spyOn(diningRoomService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ diningRoom });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(diningRoomService.update).toHaveBeenCalled();
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
  });
});
