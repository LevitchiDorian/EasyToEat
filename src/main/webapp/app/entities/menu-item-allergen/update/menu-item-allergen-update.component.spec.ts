import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IMenuItem } from 'app/entities/menu-item/menu-item.model';
import { MenuItemService } from 'app/entities/menu-item/service/menu-item.service';
import { MenuItemAllergenService } from '../service/menu-item-allergen.service';
import { IMenuItemAllergen } from '../menu-item-allergen.model';
import { MenuItemAllergenFormService } from './menu-item-allergen-form.service';

import { MenuItemAllergenUpdateComponent } from './menu-item-allergen-update.component';

describe('MenuItemAllergen Management Update Component', () => {
  let comp: MenuItemAllergenUpdateComponent;
  let fixture: ComponentFixture<MenuItemAllergenUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let menuItemAllergenFormService: MenuItemAllergenFormService;
  let menuItemAllergenService: MenuItemAllergenService;
  let menuItemService: MenuItemService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MenuItemAllergenUpdateComponent],
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
      .overrideTemplate(MenuItemAllergenUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MenuItemAllergenUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    menuItemAllergenFormService = TestBed.inject(MenuItemAllergenFormService);
    menuItemAllergenService = TestBed.inject(MenuItemAllergenService);
    menuItemService = TestBed.inject(MenuItemService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call MenuItem query and add missing value', () => {
      const menuItemAllergen: IMenuItemAllergen = { id: 17370 };
      const menuItem: IMenuItem = { id: 11248 };
      menuItemAllergen.menuItem = menuItem;

      const menuItemCollection: IMenuItem[] = [{ id: 11248 }];
      jest.spyOn(menuItemService, 'query').mockReturnValue(of(new HttpResponse({ body: menuItemCollection })));
      const additionalMenuItems = [menuItem];
      const expectedCollection: IMenuItem[] = [...additionalMenuItems, ...menuItemCollection];
      jest.spyOn(menuItemService, 'addMenuItemToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ menuItemAllergen });
      comp.ngOnInit();

      expect(menuItemService.query).toHaveBeenCalled();
      expect(menuItemService.addMenuItemToCollectionIfMissing).toHaveBeenCalledWith(
        menuItemCollection,
        ...additionalMenuItems.map(expect.objectContaining),
      );
      expect(comp.menuItemsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const menuItemAllergen: IMenuItemAllergen = { id: 17370 };
      const menuItem: IMenuItem = { id: 11248 };
      menuItemAllergen.menuItem = menuItem;

      activatedRoute.data = of({ menuItemAllergen });
      comp.ngOnInit();

      expect(comp.menuItemsSharedCollection).toContainEqual(menuItem);
      expect(comp.menuItemAllergen).toEqual(menuItemAllergen);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMenuItemAllergen>>();
      const menuItemAllergen = { id: 13353 };
      jest.spyOn(menuItemAllergenFormService, 'getMenuItemAllergen').mockReturnValue(menuItemAllergen);
      jest.spyOn(menuItemAllergenService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ menuItemAllergen });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: menuItemAllergen }));
      saveSubject.complete();

      // THEN
      expect(menuItemAllergenFormService.getMenuItemAllergen).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(menuItemAllergenService.update).toHaveBeenCalledWith(expect.objectContaining(menuItemAllergen));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMenuItemAllergen>>();
      const menuItemAllergen = { id: 13353 };
      jest.spyOn(menuItemAllergenFormService, 'getMenuItemAllergen').mockReturnValue({ id: null });
      jest.spyOn(menuItemAllergenService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ menuItemAllergen: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: menuItemAllergen }));
      saveSubject.complete();

      // THEN
      expect(menuItemAllergenFormService.getMenuItemAllergen).toHaveBeenCalled();
      expect(menuItemAllergenService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMenuItemAllergen>>();
      const menuItemAllergen = { id: 13353 };
      jest.spyOn(menuItemAllergenService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ menuItemAllergen });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(menuItemAllergenService.update).toHaveBeenCalled();
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
  });
});
