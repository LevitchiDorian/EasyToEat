import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IBrand } from 'app/entities/brand/brand.model';
import { BrandService } from 'app/entities/brand/service/brand.service';
import { MenuCategoryService } from '../service/menu-category.service';
import { IMenuCategory } from '../menu-category.model';
import { MenuCategoryFormService } from './menu-category-form.service';

import { MenuCategoryUpdateComponent } from './menu-category-update.component';

describe('MenuCategory Management Update Component', () => {
  let comp: MenuCategoryUpdateComponent;
  let fixture: ComponentFixture<MenuCategoryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let menuCategoryFormService: MenuCategoryFormService;
  let menuCategoryService: MenuCategoryService;
  let brandService: BrandService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MenuCategoryUpdateComponent],
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
      .overrideTemplate(MenuCategoryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MenuCategoryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    menuCategoryFormService = TestBed.inject(MenuCategoryFormService);
    menuCategoryService = TestBed.inject(MenuCategoryService);
    brandService = TestBed.inject(BrandService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call MenuCategory query and add missing value', () => {
      const menuCategory: IMenuCategory = { id: 8714 };
      const parent: IMenuCategory = { id: 8033 };
      menuCategory.parent = parent;

      const menuCategoryCollection: IMenuCategory[] = [{ id: 8033 }];
      jest.spyOn(menuCategoryService, 'query').mockReturnValue(of(new HttpResponse({ body: menuCategoryCollection })));
      const additionalMenuCategories = [parent];
      const expectedCollection: IMenuCategory[] = [...additionalMenuCategories, ...menuCategoryCollection];
      jest.spyOn(menuCategoryService, 'addMenuCategoryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ menuCategory });
      comp.ngOnInit();

      expect(menuCategoryService.query).toHaveBeenCalled();
      expect(menuCategoryService.addMenuCategoryToCollectionIfMissing).toHaveBeenCalledWith(
        menuCategoryCollection,
        ...additionalMenuCategories.map(expect.objectContaining),
      );
      expect(comp.menuCategoriesSharedCollection).toEqual(expectedCollection);
    });

    it('should call Brand query and add missing value', () => {
      const menuCategory: IMenuCategory = { id: 8714 };
      const brand: IBrand = { id: 7763 };
      menuCategory.brand = brand;

      const brandCollection: IBrand[] = [{ id: 7763 }];
      jest.spyOn(brandService, 'query').mockReturnValue(of(new HttpResponse({ body: brandCollection })));
      const additionalBrands = [brand];
      const expectedCollection: IBrand[] = [...additionalBrands, ...brandCollection];
      jest.spyOn(brandService, 'addBrandToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ menuCategory });
      comp.ngOnInit();

      expect(brandService.query).toHaveBeenCalled();
      expect(brandService.addBrandToCollectionIfMissing).toHaveBeenCalledWith(
        brandCollection,
        ...additionalBrands.map(expect.objectContaining),
      );
      expect(comp.brandsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const menuCategory: IMenuCategory = { id: 8714 };
      const parent: IMenuCategory = { id: 8033 };
      menuCategory.parent = parent;
      const brand: IBrand = { id: 7763 };
      menuCategory.brand = brand;

      activatedRoute.data = of({ menuCategory });
      comp.ngOnInit();

      expect(comp.menuCategoriesSharedCollection).toContainEqual(parent);
      expect(comp.brandsSharedCollection).toContainEqual(brand);
      expect(comp.menuCategory).toEqual(menuCategory);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMenuCategory>>();
      const menuCategory = { id: 8033 };
      jest.spyOn(menuCategoryFormService, 'getMenuCategory').mockReturnValue(menuCategory);
      jest.spyOn(menuCategoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ menuCategory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: menuCategory }));
      saveSubject.complete();

      // THEN
      expect(menuCategoryFormService.getMenuCategory).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(menuCategoryService.update).toHaveBeenCalledWith(expect.objectContaining(menuCategory));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMenuCategory>>();
      const menuCategory = { id: 8033 };
      jest.spyOn(menuCategoryFormService, 'getMenuCategory').mockReturnValue({ id: null });
      jest.spyOn(menuCategoryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ menuCategory: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: menuCategory }));
      saveSubject.complete();

      // THEN
      expect(menuCategoryFormService.getMenuCategory).toHaveBeenCalled();
      expect(menuCategoryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMenuCategory>>();
      const menuCategory = { id: 8033 };
      jest.spyOn(menuCategoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ menuCategory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(menuCategoryService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareMenuCategory', () => {
      it('should forward to menuCategoryService', () => {
        const entity = { id: 8033 };
        const entity2 = { id: 8714 };
        jest.spyOn(menuCategoryService, 'compareMenuCategory');
        comp.compareMenuCategory(entity, entity2);
        expect(menuCategoryService.compareMenuCategory).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareBrand', () => {
      it('should forward to brandService', () => {
        const entity = { id: 7763 };
        const entity2 = { id: 6898 };
        jest.spyOn(brandService, 'compareBrand');
        comp.compareBrand(entity, entity2);
        expect(brandService.compareBrand).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
