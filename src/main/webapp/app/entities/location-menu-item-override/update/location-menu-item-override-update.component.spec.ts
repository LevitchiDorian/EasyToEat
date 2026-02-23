import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IMenuItem } from 'app/entities/menu-item/menu-item.model';
import { MenuItemService } from 'app/entities/menu-item/service/menu-item.service';
import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';
import { ILocationMenuItemOverride } from '../location-menu-item-override.model';
import { LocationMenuItemOverrideService } from '../service/location-menu-item-override.service';
import { LocationMenuItemOverrideFormService } from './location-menu-item-override-form.service';

import { LocationMenuItemOverrideUpdateComponent } from './location-menu-item-override-update.component';

describe('LocationMenuItemOverride Management Update Component', () => {
  let comp: LocationMenuItemOverrideUpdateComponent;
  let fixture: ComponentFixture<LocationMenuItemOverrideUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let locationMenuItemOverrideFormService: LocationMenuItemOverrideFormService;
  let locationMenuItemOverrideService: LocationMenuItemOverrideService;
  let menuItemService: MenuItemService;
  let locationService: LocationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [LocationMenuItemOverrideUpdateComponent],
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
      .overrideTemplate(LocationMenuItemOverrideUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LocationMenuItemOverrideUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    locationMenuItemOverrideFormService = TestBed.inject(LocationMenuItemOverrideFormService);
    locationMenuItemOverrideService = TestBed.inject(LocationMenuItemOverrideService);
    menuItemService = TestBed.inject(MenuItemService);
    locationService = TestBed.inject(LocationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call MenuItem query and add missing value', () => {
      const locationMenuItemOverride: ILocationMenuItemOverride = { id: 10167 };
      const menuItem: IMenuItem = { id: 11248 };
      locationMenuItemOverride.menuItem = menuItem;

      const menuItemCollection: IMenuItem[] = [{ id: 11248 }];
      jest.spyOn(menuItemService, 'query').mockReturnValue(of(new HttpResponse({ body: menuItemCollection })));
      const additionalMenuItems = [menuItem];
      const expectedCollection: IMenuItem[] = [...additionalMenuItems, ...menuItemCollection];
      jest.spyOn(menuItemService, 'addMenuItemToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ locationMenuItemOverride });
      comp.ngOnInit();

      expect(menuItemService.query).toHaveBeenCalled();
      expect(menuItemService.addMenuItemToCollectionIfMissing).toHaveBeenCalledWith(
        menuItemCollection,
        ...additionalMenuItems.map(expect.objectContaining),
      );
      expect(comp.menuItemsSharedCollection).toEqual(expectedCollection);
    });

    it('should call Location query and add missing value', () => {
      const locationMenuItemOverride: ILocationMenuItemOverride = { id: 10167 };
      const location: ILocation = { id: 8454 };
      locationMenuItemOverride.location = location;

      const locationCollection: ILocation[] = [{ id: 8454 }];
      jest.spyOn(locationService, 'query').mockReturnValue(of(new HttpResponse({ body: locationCollection })));
      const additionalLocations = [location];
      const expectedCollection: ILocation[] = [...additionalLocations, ...locationCollection];
      jest.spyOn(locationService, 'addLocationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ locationMenuItemOverride });
      comp.ngOnInit();

      expect(locationService.query).toHaveBeenCalled();
      expect(locationService.addLocationToCollectionIfMissing).toHaveBeenCalledWith(
        locationCollection,
        ...additionalLocations.map(expect.objectContaining),
      );
      expect(comp.locationsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const locationMenuItemOverride: ILocationMenuItemOverride = { id: 10167 };
      const menuItem: IMenuItem = { id: 11248 };
      locationMenuItemOverride.menuItem = menuItem;
      const location: ILocation = { id: 8454 };
      locationMenuItemOverride.location = location;

      activatedRoute.data = of({ locationMenuItemOverride });
      comp.ngOnInit();

      expect(comp.menuItemsSharedCollection).toContainEqual(menuItem);
      expect(comp.locationsSharedCollection).toContainEqual(location);
      expect(comp.locationMenuItemOverride).toEqual(locationMenuItemOverride);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILocationMenuItemOverride>>();
      const locationMenuItemOverride = { id: 27299 };
      jest.spyOn(locationMenuItemOverrideFormService, 'getLocationMenuItemOverride').mockReturnValue(locationMenuItemOverride);
      jest.spyOn(locationMenuItemOverrideService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ locationMenuItemOverride });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: locationMenuItemOverride }));
      saveSubject.complete();

      // THEN
      expect(locationMenuItemOverrideFormService.getLocationMenuItemOverride).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(locationMenuItemOverrideService.update).toHaveBeenCalledWith(expect.objectContaining(locationMenuItemOverride));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILocationMenuItemOverride>>();
      const locationMenuItemOverride = { id: 27299 };
      jest.spyOn(locationMenuItemOverrideFormService, 'getLocationMenuItemOverride').mockReturnValue({ id: null });
      jest.spyOn(locationMenuItemOverrideService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ locationMenuItemOverride: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: locationMenuItemOverride }));
      saveSubject.complete();

      // THEN
      expect(locationMenuItemOverrideFormService.getLocationMenuItemOverride).toHaveBeenCalled();
      expect(locationMenuItemOverrideService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILocationMenuItemOverride>>();
      const locationMenuItemOverride = { id: 27299 };
      jest.spyOn(locationMenuItemOverrideService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ locationMenuItemOverride });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(locationMenuItemOverrideService.update).toHaveBeenCalled();
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
