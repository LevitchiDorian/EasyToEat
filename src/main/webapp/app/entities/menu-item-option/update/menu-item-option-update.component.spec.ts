import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IMenuItem } from 'app/entities/menu-item/menu-item.model';
import { MenuItemService } from 'app/entities/menu-item/service/menu-item.service';
import { MenuItemOptionService } from '../service/menu-item-option.service';
import { IMenuItemOption } from '../menu-item-option.model';
import { MenuItemOptionFormService } from './menu-item-option-form.service';

import { MenuItemOptionUpdateComponent } from './menu-item-option-update.component';

describe('MenuItemOption Management Update Component', () => {
  let comp: MenuItemOptionUpdateComponent;
  let fixture: ComponentFixture<MenuItemOptionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let menuItemOptionFormService: MenuItemOptionFormService;
  let menuItemOptionService: MenuItemOptionService;
  let menuItemService: MenuItemService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MenuItemOptionUpdateComponent],
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
      .overrideTemplate(MenuItemOptionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MenuItemOptionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    menuItemOptionFormService = TestBed.inject(MenuItemOptionFormService);
    menuItemOptionService = TestBed.inject(MenuItemOptionService);
    menuItemService = TestBed.inject(MenuItemService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call MenuItem query and add missing value', () => {
      const menuItemOption: IMenuItemOption = { id: 195 };
      const menuItem: IMenuItem = { id: 11248 };
      menuItemOption.menuItem = menuItem;

      const menuItemCollection: IMenuItem[] = [{ id: 11248 }];
      jest.spyOn(menuItemService, 'query').mockReturnValue(of(new HttpResponse({ body: menuItemCollection })));
      const additionalMenuItems = [menuItem];
      const expectedCollection: IMenuItem[] = [...additionalMenuItems, ...menuItemCollection];
      jest.spyOn(menuItemService, 'addMenuItemToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ menuItemOption });
      comp.ngOnInit();

      expect(menuItemService.query).toHaveBeenCalled();
      expect(menuItemService.addMenuItemToCollectionIfMissing).toHaveBeenCalledWith(
        menuItemCollection,
        ...additionalMenuItems.map(expect.objectContaining),
      );
      expect(comp.menuItemsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const menuItemOption: IMenuItemOption = { id: 195 };
      const menuItem: IMenuItem = { id: 11248 };
      menuItemOption.menuItem = menuItem;

      activatedRoute.data = of({ menuItemOption });
      comp.ngOnInit();

      expect(comp.menuItemsSharedCollection).toContainEqual(menuItem);
      expect(comp.menuItemOption).toEqual(menuItemOption);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMenuItemOption>>();
      const menuItemOption = { id: 20913 };
      jest.spyOn(menuItemOptionFormService, 'getMenuItemOption').mockReturnValue(menuItemOption);
      jest.spyOn(menuItemOptionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ menuItemOption });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: menuItemOption }));
      saveSubject.complete();

      // THEN
      expect(menuItemOptionFormService.getMenuItemOption).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(menuItemOptionService.update).toHaveBeenCalledWith(expect.objectContaining(menuItemOption));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMenuItemOption>>();
      const menuItemOption = { id: 20913 };
      jest.spyOn(menuItemOptionFormService, 'getMenuItemOption').mockReturnValue({ id: null });
      jest.spyOn(menuItemOptionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ menuItemOption: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: menuItemOption }));
      saveSubject.complete();

      // THEN
      expect(menuItemOptionFormService.getMenuItemOption).toHaveBeenCalled();
      expect(menuItemOptionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMenuItemOption>>();
      const menuItemOption = { id: 20913 };
      jest.spyOn(menuItemOptionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ menuItemOption });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(menuItemOptionService.update).toHaveBeenCalled();
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
