import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IDiningRoom } from 'app/entities/dining-room/dining-room.model';
import { DiningRoomService } from 'app/entities/dining-room/service/dining-room.service';
import { IWaitingList } from '../waiting-list.model';
import { WaitingListService } from '../service/waiting-list.service';
import { WaitingListFormService } from './waiting-list-form.service';

import { WaitingListUpdateComponent } from './waiting-list-update.component';

describe('WaitingList Management Update Component', () => {
  let comp: WaitingListUpdateComponent;
  let fixture: ComponentFixture<WaitingListUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let waitingListFormService: WaitingListFormService;
  let waitingListService: WaitingListService;
  let locationService: LocationService;
  let userService: UserService;
  let diningRoomService: DiningRoomService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [WaitingListUpdateComponent],
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
      .overrideTemplate(WaitingListUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(WaitingListUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    waitingListFormService = TestBed.inject(WaitingListFormService);
    waitingListService = TestBed.inject(WaitingListService);
    locationService = TestBed.inject(LocationService);
    userService = TestBed.inject(UserService);
    diningRoomService = TestBed.inject(DiningRoomService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Location query and add missing value', () => {
      const waitingList: IWaitingList = { id: 28180 };
      const location: ILocation = { id: 8454 };
      waitingList.location = location;

      const locationCollection: ILocation[] = [{ id: 8454 }];
      jest.spyOn(locationService, 'query').mockReturnValue(of(new HttpResponse({ body: locationCollection })));
      const additionalLocations = [location];
      const expectedCollection: ILocation[] = [...additionalLocations, ...locationCollection];
      jest.spyOn(locationService, 'addLocationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ waitingList });
      comp.ngOnInit();

      expect(locationService.query).toHaveBeenCalled();
      expect(locationService.addLocationToCollectionIfMissing).toHaveBeenCalledWith(
        locationCollection,
        ...additionalLocations.map(expect.objectContaining),
      );
      expect(comp.locationsSharedCollection).toEqual(expectedCollection);
    });

    it('should call User query and add missing value', () => {
      const waitingList: IWaitingList = { id: 28180 };
      const client: IUser = { id: 3944 };
      waitingList.client = client;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [client];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ waitingList });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('should call DiningRoom query and add missing value', () => {
      const waitingList: IWaitingList = { id: 28180 };
      const room: IDiningRoom = { id: 3993 };
      waitingList.room = room;

      const diningRoomCollection: IDiningRoom[] = [{ id: 3993 }];
      jest.spyOn(diningRoomService, 'query').mockReturnValue(of(new HttpResponse({ body: diningRoomCollection })));
      const additionalDiningRooms = [room];
      const expectedCollection: IDiningRoom[] = [...additionalDiningRooms, ...diningRoomCollection];
      jest.spyOn(diningRoomService, 'addDiningRoomToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ waitingList });
      comp.ngOnInit();

      expect(diningRoomService.query).toHaveBeenCalled();
      expect(diningRoomService.addDiningRoomToCollectionIfMissing).toHaveBeenCalledWith(
        diningRoomCollection,
        ...additionalDiningRooms.map(expect.objectContaining),
      );
      expect(comp.diningRoomsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const waitingList: IWaitingList = { id: 28180 };
      const location: ILocation = { id: 8454 };
      waitingList.location = location;
      const client: IUser = { id: 3944 };
      waitingList.client = client;
      const room: IDiningRoom = { id: 3993 };
      waitingList.room = room;

      activatedRoute.data = of({ waitingList });
      comp.ngOnInit();

      expect(comp.locationsSharedCollection).toContainEqual(location);
      expect(comp.usersSharedCollection).toContainEqual(client);
      expect(comp.diningRoomsSharedCollection).toContainEqual(room);
      expect(comp.waitingList).toEqual(waitingList);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IWaitingList>>();
      const waitingList = { id: 15410 };
      jest.spyOn(waitingListFormService, 'getWaitingList').mockReturnValue(waitingList);
      jest.spyOn(waitingListService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ waitingList });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: waitingList }));
      saveSubject.complete();

      // THEN
      expect(waitingListFormService.getWaitingList).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(waitingListService.update).toHaveBeenCalledWith(expect.objectContaining(waitingList));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IWaitingList>>();
      const waitingList = { id: 15410 };
      jest.spyOn(waitingListFormService, 'getWaitingList').mockReturnValue({ id: null });
      jest.spyOn(waitingListService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ waitingList: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: waitingList }));
      saveSubject.complete();

      // THEN
      expect(waitingListFormService.getWaitingList).toHaveBeenCalled();
      expect(waitingListService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IWaitingList>>();
      const waitingList = { id: 15410 };
      jest.spyOn(waitingListService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ waitingList });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(waitingListService.update).toHaveBeenCalled();
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

    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareDiningRoom', () => {
      it('should forward to diningRoomService', () => {
        const entity = { id: 3993 };
        const entity2 = { id: 12477 };
        jest.spyOn(diningRoomService, 'compareDiningRoom');
        comp.compareDiningRoom(entity, entity2);
        expect(diningRoomService.compareDiningRoom).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
