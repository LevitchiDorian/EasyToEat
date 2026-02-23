import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IMenuItemOption } from 'app/entities/menu-item-option/menu-item-option.model';
import { MenuItemOptionService } from 'app/entities/menu-item-option/service/menu-item-option.service';
import { MenuItemOptionValueService } from '../service/menu-item-option-value.service';
import { IMenuItemOptionValue } from '../menu-item-option-value.model';
import { MenuItemOptionValueFormService } from './menu-item-option-value-form.service';

import { MenuItemOptionValueUpdateComponent } from './menu-item-option-value-update.component';

describe('MenuItemOptionValue Management Update Component', () => {
  let comp: MenuItemOptionValueUpdateComponent;
  let fixture: ComponentFixture<MenuItemOptionValueUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let menuItemOptionValueFormService: MenuItemOptionValueFormService;
  let menuItemOptionValueService: MenuItemOptionValueService;
  let menuItemOptionService: MenuItemOptionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MenuItemOptionValueUpdateComponent],
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
      .overrideTemplate(MenuItemOptionValueUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MenuItemOptionValueUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    menuItemOptionValueFormService = TestBed.inject(MenuItemOptionValueFormService);
    menuItemOptionValueService = TestBed.inject(MenuItemOptionValueService);
    menuItemOptionService = TestBed.inject(MenuItemOptionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call MenuItemOption query and add missing value', () => {
      const menuItemOptionValue: IMenuItemOptionValue = { id: 30184 };
      const option: IMenuItemOption = { id: 20913 };
      menuItemOptionValue.option = option;

      const menuItemOptionCollection: IMenuItemOption[] = [{ id: 20913 }];
      jest.spyOn(menuItemOptionService, 'query').mockReturnValue(of(new HttpResponse({ body: menuItemOptionCollection })));
      const additionalMenuItemOptions = [option];
      const expectedCollection: IMenuItemOption[] = [...additionalMenuItemOptions, ...menuItemOptionCollection];
      jest.spyOn(menuItemOptionService, 'addMenuItemOptionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ menuItemOptionValue });
      comp.ngOnInit();

      expect(menuItemOptionService.query).toHaveBeenCalled();
      expect(menuItemOptionService.addMenuItemOptionToCollectionIfMissing).toHaveBeenCalledWith(
        menuItemOptionCollection,
        ...additionalMenuItemOptions.map(expect.objectContaining),
      );
      expect(comp.menuItemOptionsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const menuItemOptionValue: IMenuItemOptionValue = { id: 30184 };
      const option: IMenuItemOption = { id: 20913 };
      menuItemOptionValue.option = option;

      activatedRoute.data = of({ menuItemOptionValue });
      comp.ngOnInit();

      expect(comp.menuItemOptionsSharedCollection).toContainEqual(option);
      expect(comp.menuItemOptionValue).toEqual(menuItemOptionValue);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMenuItemOptionValue>>();
      const menuItemOptionValue = { id: 31564 };
      jest.spyOn(menuItemOptionValueFormService, 'getMenuItemOptionValue').mockReturnValue(menuItemOptionValue);
      jest.spyOn(menuItemOptionValueService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ menuItemOptionValue });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: menuItemOptionValue }));
      saveSubject.complete();

      // THEN
      expect(menuItemOptionValueFormService.getMenuItemOptionValue).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(menuItemOptionValueService.update).toHaveBeenCalledWith(expect.objectContaining(menuItemOptionValue));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMenuItemOptionValue>>();
      const menuItemOptionValue = { id: 31564 };
      jest.spyOn(menuItemOptionValueFormService, 'getMenuItemOptionValue').mockReturnValue({ id: null });
      jest.spyOn(menuItemOptionValueService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ menuItemOptionValue: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: menuItemOptionValue }));
      saveSubject.complete();

      // THEN
      expect(menuItemOptionValueFormService.getMenuItemOptionValue).toHaveBeenCalled();
      expect(menuItemOptionValueService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMenuItemOptionValue>>();
      const menuItemOptionValue = { id: 31564 };
      jest.spyOn(menuItemOptionValueService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ menuItemOptionValue });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(menuItemOptionValueService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareMenuItemOption', () => {
      it('should forward to menuItemOptionService', () => {
        const entity = { id: 20913 };
        const entity2 = { id: 195 };
        jest.spyOn(menuItemOptionService, 'compareMenuItemOption');
        comp.compareMenuItemOption(entity, entity2);
        expect(menuItemOptionService.compareMenuItemOption).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
