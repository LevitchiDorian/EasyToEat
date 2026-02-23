import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IBrand } from 'app/entities/brand/brand.model';
import { BrandService } from 'app/entities/brand/service/brand.service';
import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';
import { IPromotion } from '../promotion.model';
import { PromotionService } from '../service/promotion.service';
import { PromotionFormService } from './promotion-form.service';

import { PromotionUpdateComponent } from './promotion-update.component';

describe('Promotion Management Update Component', () => {
  let comp: PromotionUpdateComponent;
  let fixture: ComponentFixture<PromotionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let promotionFormService: PromotionFormService;
  let promotionService: PromotionService;
  let brandService: BrandService;
  let locationService: LocationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PromotionUpdateComponent],
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
      .overrideTemplate(PromotionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PromotionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    promotionFormService = TestBed.inject(PromotionFormService);
    promotionService = TestBed.inject(PromotionService);
    brandService = TestBed.inject(BrandService);
    locationService = TestBed.inject(LocationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Brand query and add missing value', () => {
      const promotion: IPromotion = { id: 21560 };
      const brand: IBrand = { id: 7763 };
      promotion.brand = brand;

      const brandCollection: IBrand[] = [{ id: 7763 }];
      jest.spyOn(brandService, 'query').mockReturnValue(of(new HttpResponse({ body: brandCollection })));
      const additionalBrands = [brand];
      const expectedCollection: IBrand[] = [...additionalBrands, ...brandCollection];
      jest.spyOn(brandService, 'addBrandToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ promotion });
      comp.ngOnInit();

      expect(brandService.query).toHaveBeenCalled();
      expect(brandService.addBrandToCollectionIfMissing).toHaveBeenCalledWith(
        brandCollection,
        ...additionalBrands.map(expect.objectContaining),
      );
      expect(comp.brandsSharedCollection).toEqual(expectedCollection);
    });

    it('should call Location query and add missing value', () => {
      const promotion: IPromotion = { id: 21560 };
      const location: ILocation = { id: 8454 };
      promotion.location = location;

      const locationCollection: ILocation[] = [{ id: 8454 }];
      jest.spyOn(locationService, 'query').mockReturnValue(of(new HttpResponse({ body: locationCollection })));
      const additionalLocations = [location];
      const expectedCollection: ILocation[] = [...additionalLocations, ...locationCollection];
      jest.spyOn(locationService, 'addLocationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ promotion });
      comp.ngOnInit();

      expect(locationService.query).toHaveBeenCalled();
      expect(locationService.addLocationToCollectionIfMissing).toHaveBeenCalledWith(
        locationCollection,
        ...additionalLocations.map(expect.objectContaining),
      );
      expect(comp.locationsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const promotion: IPromotion = { id: 21560 };
      const brand: IBrand = { id: 7763 };
      promotion.brand = brand;
      const location: ILocation = { id: 8454 };
      promotion.location = location;

      activatedRoute.data = of({ promotion });
      comp.ngOnInit();

      expect(comp.brandsSharedCollection).toContainEqual(brand);
      expect(comp.locationsSharedCollection).toContainEqual(location);
      expect(comp.promotion).toEqual(promotion);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPromotion>>();
      const promotion = { id: 30739 };
      jest.spyOn(promotionFormService, 'getPromotion').mockReturnValue(promotion);
      jest.spyOn(promotionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ promotion });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: promotion }));
      saveSubject.complete();

      // THEN
      expect(promotionFormService.getPromotion).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(promotionService.update).toHaveBeenCalledWith(expect.objectContaining(promotion));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPromotion>>();
      const promotion = { id: 30739 };
      jest.spyOn(promotionFormService, 'getPromotion').mockReturnValue({ id: null });
      jest.spyOn(promotionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ promotion: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: promotion }));
      saveSubject.complete();

      // THEN
      expect(promotionFormService.getPromotion).toHaveBeenCalled();
      expect(promotionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPromotion>>();
      const promotion = { id: 30739 };
      jest.spyOn(promotionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ promotion });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(promotionService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareBrand', () => {
      it('should forward to brandService', () => {
        const entity = { id: 7763 };
        const entity2 = { id: 6898 };
        jest.spyOn(brandService, 'compareBrand');
        comp.compareBrand(entity, entity2);
        expect(brandService.compareBrand).toHaveBeenCalledWith(entity, entity2);
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
