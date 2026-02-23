jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, fakeAsync, inject, tick } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { MenuItemOptionValueService } from '../service/menu-item-option-value.service';

import { MenuItemOptionValueDeleteDialogComponent } from './menu-item-option-value-delete-dialog.component';

describe('MenuItemOptionValue Management Delete Component', () => {
  let comp: MenuItemOptionValueDeleteDialogComponent;
  let fixture: ComponentFixture<MenuItemOptionValueDeleteDialogComponent>;
  let service: MenuItemOptionValueService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MenuItemOptionValueDeleteDialogComponent],
      providers: [provideHttpClient(), NgbActiveModal],
    })
      .overrideTemplate(MenuItemOptionValueDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(MenuItemOptionValueDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(MenuItemOptionValueService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));

        // WHEN
        comp.confirmDelete(123);
        tick();

        // THEN
        expect(service.delete).toHaveBeenCalledWith(123);
        expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
      }),
    ));

    it('should not call delete service on clear', () => {
      // GIVEN
      jest.spyOn(service, 'delete');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
