import { Component, OnInit, inject, signal } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';

import SharedModule from 'app/shared/shared.module';
import { LANGUAGES } from 'app/config/language.constants';
import { IUser } from '../user-management.model';
import { UserManagementService } from '../service/user-management.service';
import { ApplicationConfigService } from 'app/core/config/application-config.service';

interface LocationOption {
  id: number;
  name: string;
  address?: string;
}

interface UserProfileDTO {
  id: number;
  location?: { id: number; name?: string };
}

const userTemplate = {} as IUser;

const newUser: IUser = {
  langKey: 'en',
  activated: true,
} as IUser;

@Component({
  selector: 'jhi-user-mgmt-update',
  templateUrl: './user-management-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export default class UserManagementUpdateComponent implements OnInit {
  languages = LANGUAGES;
  authorities = signal<string[]>([]);
  isSaving = signal(false);
  locations = signal<LocationOption[]>([]);
  selectedLocationId = signal<number | null>(null);

  editForm = new FormGroup({
    id: new FormControl(userTemplate.id),
    login: new FormControl(userTemplate.login, {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(50),
        Validators.pattern('^[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^[_.@A-Za-z0-9-]+$'),
      ],
    }),
    firstName: new FormControl(userTemplate.firstName, { validators: [Validators.maxLength(50)] }),
    lastName: new FormControl(userTemplate.lastName, { validators: [Validators.maxLength(50)] }),
    email: new FormControl(userTemplate.email, {
      nonNullable: true,
      validators: [Validators.minLength(5), Validators.maxLength(254), Validators.email],
    }),
    activated: new FormControl(userTemplate.activated, { nonNullable: true }),
    langKey: new FormControl(userTemplate.langKey, { nonNullable: true }),
    authorities: new FormControl(userTemplate.authorities, { nonNullable: true }),
  });

  private readonly userService = inject(UserManagementService);
  private readonly route = inject(ActivatedRoute);
  private readonly http = inject(HttpClient);
  private readonly configService = inject(ApplicationConfigService);

  ngOnInit(): void {
    this.route.data.subscribe(({ user }) => {
      if (user) {
        this.editForm.reset(user);
        // Load the user's current location if MANAGER or STAFF
        if (user.id) {
          this.loadUserLocation(user.id);
        }
      } else {
        this.editForm.reset(newUser);
      }
    });
    this.userService.authorities().subscribe(authorities => this.authorities.set(authorities));
    this.loadLocations();
  }

  get selectedAuthority(): string {
    const auths: string[] = this.editForm.get('authorities')?.value ?? [];
    return auths[0] ?? '';
  }

  get needsLocation(): boolean {
    return this.selectedAuthority === 'ROLE_MANAGER' || this.selectedAuthority === 'ROLE_STAFF';
  }

  selectAuthority(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.editForm.get('authorities')?.setValue(value ? [value] : []);
    if (value !== 'ROLE_MANAGER' && value !== 'ROLE_STAFF') {
      this.selectedLocationId.set(null);
    }
  }

  selectLocation(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.selectedLocationId.set(value ? +value : null);
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const user = this.editForm.getRawValue();
    if (user.id !== null) {
      this.userService.update(user).subscribe({
        next: () => this.assignLocationThenFinish(user.login ?? ''),
        error: () => this.onSaveError(),
      });
    } else {
      this.userService.create(user).subscribe({
        next: createdUser => this.assignLocationThenFinish((createdUser as IUser).login ?? user.login ?? ''),
        error: () => this.onSaveError(),
      });
    }
  }

  private loadLocations(): void {
    this.http
      .get<LocationOption[]>(this.configService.getEndpointFor('api/locations?size=200&sort=name,asc'))
      .subscribe({ next: locs => this.locations.set(locs), error: () => {} });
  }

  private loadUserLocation(userId: number): void {
    this.http.get<UserProfileDTO[]>(this.configService.getEndpointFor(`api/user-profiles?userId.equals=${userId}`)).subscribe({
      next: profiles => {
        if (profiles.length > 0 && profiles[0].location?.id) {
          this.selectedLocationId.set(profiles[0].location.id);
        }
      },
      error: () => {},
    });
  }

  private assignLocationThenFinish(login: string): void {
    if (!this.needsLocation || !login) {
      this.onSaveSuccess();
      return;
    }
    const locId = this.selectedLocationId();
    this.http
      .patch(this.configService.getEndpointFor(`api/admin/users/${login}/location`), { locationId: locId })
      .subscribe({ next: () => this.onSaveSuccess(), error: () => this.onSaveSuccess() });
  }

  private onSaveSuccess(): void {
    this.isSaving.set(false);
    this.previousState();
  }

  private onSaveError(): void {
    this.isSaving.set(false);
  }
}
