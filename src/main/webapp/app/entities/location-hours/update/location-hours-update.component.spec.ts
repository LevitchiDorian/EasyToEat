import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';
import { LocationHoursService } from '../service/location-hours.service';
import { ILocationHours } from '../location-hours.model';
import { LocationHoursFormService } from './location-hours-form.service';

import { LocationHoursUpdateComponent } from './location-hours-update.component';

describe('LocationHours Management Update Component', () => {
  let comp: LocationHoursUpdateComponent;
  let fixture: ComponentFixture<LocationHoursUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let locationHoursFormService: LocationHoursFormService;
  let locationHoursService: LocationHoursService;
  let locationService: LocationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [LocationHoursUpdateComponent],
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
      .overrideTemplate(LocationHoursUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LocationHoursUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    locationHoursFormService = TestBed.inject(LocationHoursFormService);
    locationHoursService = TestBed.inject(LocationHoursService);
    locationService = TestBed.inject(LocationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Location query and add missing value', () => {
      const locationHours: ILocationHours = { id: 29297 };
      const location: ILocation = { id: 8454 };
      locationHours.location = location;

      const locationCollection: ILocation[] = [{ id: 8454 }];
      jest.spyOn(locationService, 'query').mockReturnValue(of(new HttpResponse({ body: locationCollection })));
      const additionalLocations = [location];
      const expectedCollection: ILocation[] = [...additionalLocations, ...locationCollection];
      jest.spyOn(locationService, 'addLocationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ locationHours });
      comp.ngOnInit();

      expect(locationService.query).toHaveBeenCalled();
      expect(locationService.addLocationToCollectionIfMissing).toHaveBeenCalledWith(
        locationCollection,
        ...additionalLocations.map(expect.objectContaining),
      );
      expect(comp.locationsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const locationHours: ILocationHours = { id: 29297 };
      const location: ILocation = { id: 8454 };
      locationHours.location = location;

      activatedRoute.data = of({ locationHours });
      comp.ngOnInit();

      expect(comp.locationsSharedCollection).toContainEqual(location);
      expect(comp.locationHours).toEqual(locationHours);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILocationHours>>();
      const locationHours = { id: 18708 };
      jest.spyOn(locationHoursFormService, 'getLocationHours').mockReturnValue(locationHours);
      jest.spyOn(locationHoursService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ locationHours });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: locationHours }));
      saveSubject.complete();

      // THEN
      expect(locationHoursFormService.getLocationHours).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(locationHoursService.update).toHaveBeenCalledWith(expect.objectContaining(locationHours));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILocationHours>>();
      const locationHours = { id: 18708 };
      jest.spyOn(locationHoursFormService, 'getLocationHours').mockReturnValue({ id: null });
      jest.spyOn(locationHoursService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ locationHours: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: locationHours }));
      saveSubject.complete();

      // THEN
      expect(locationHoursFormService.getLocationHours).toHaveBeenCalled();
      expect(locationHoursService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILocationHours>>();
      const locationHours = { id: 18708 };
      jest.spyOn(locationHoursService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ locationHours });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(locationHoursService.update).toHaveBeenCalled();
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
