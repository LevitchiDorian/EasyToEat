import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IMenuCategory } from 'app/entities/menu-category/menu-category.model';
import { MenuCategoryService } from 'app/entities/menu-category/service/menu-category.service';
import { MenuItemService } from '../service/menu-item.service';
import { IMenuItem } from '../menu-item.model';
import { MenuItemFormService } from './menu-item-form.service';

import { MenuItemUpdateComponent } from './menu-item-update.component';

describe('MenuItem Management Update Component', () => {
  let comp: MenuItemUpdateComponent;
  let fixture: ComponentFixture<MenuItemUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let menuItemFormService: MenuItemFormService;
  let menuItemService: MenuItemService;
  let menuCategoryService: MenuCategoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MenuItemUpdateComponent],
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
      .overrideTemplate(MenuItemUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MenuItemUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    menuItemFormService = TestBed.inject(MenuItemFormService);
    menuItemService = TestBed.inject(MenuItemService);
    menuCategoryService = TestBed.inject(MenuCategoryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call MenuCategory query and add missing value', () => {
      const menuItem: IMenuItem = { id: 12993 };
      const category: IMenuCategory = { id: 8033 };
      menuItem.category = category;

      const menuCategoryCollection: IMenuCategory[] = [{ id: 8033 }];
      jest.spyOn(menuCategoryService, 'query').mockReturnValue(of(new HttpResponse({ body: menuCategoryCollection })));
      const additionalMenuCategories = [category];
      const expectedCollection: IMenuCategory[] = [...additionalMenuCategories, ...menuCategoryCollection];
      jest.spyOn(menuCategoryService, 'addMenuCategoryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ menuItem });
      comp.ngOnInit();

      expect(menuCategoryService.query).toHaveBeenCalled();
      expect(menuCategoryService.addMenuCategoryToCollectionIfMissing).toHaveBeenCalledWith(
        menuCategoryCollection,
        ...additionalMenuCategories.map(expect.objectContaining),
      );
      expect(comp.menuCategoriesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const menuItem: IMenuItem = { id: 12993 };
      const category: IMenuCategory = { id: 8033 };
      menuItem.category = category;

      activatedRoute.data = of({ menuItem });
      comp.ngOnInit();

      expect(comp.menuCategoriesSharedCollection).toContainEqual(category);
      expect(comp.menuItem).toEqual(menuItem);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMenuItem>>();
      const menuItem = { id: 11248 };
      jest.spyOn(menuItemFormService, 'getMenuItem').mockReturnValue(menuItem);
      jest.spyOn(menuItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ menuItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: menuItem }));
      saveSubject.complete();

      // THEN
      expect(menuItemFormService.getMenuItem).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(menuItemService.update).toHaveBeenCalledWith(expect.objectContaining(menuItem));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMenuItem>>();
      const menuItem = { id: 11248 };
      jest.spyOn(menuItemFormService, 'getMenuItem').mockReturnValue({ id: null });
      jest.spyOn(menuItemService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ menuItem: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: menuItem }));
      saveSubject.complete();

      // THEN
      expect(menuItemFormService.getMenuItem).toHaveBeenCalled();
      expect(menuItemService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMenuItem>>();
      const menuItem = { id: 11248 };
      jest.spyOn(menuItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ menuItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(menuItemService.update).toHaveBeenCalled();
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
  });
});
