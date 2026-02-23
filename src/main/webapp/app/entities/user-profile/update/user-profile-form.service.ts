import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IUserProfile, NewUserProfile } from '../user-profile.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IUserProfile for edit and NewUserProfileFormGroupInput for create.
 */
type UserProfileFormGroupInput = IUserProfile | PartialWithRequiredKeyOf<NewUserProfile>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IUserProfile | NewUserProfile> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

type UserProfileFormRawValue = FormValueOf<IUserProfile>;

type NewUserProfileFormRawValue = FormValueOf<NewUserProfile>;

type UserProfileFormDefaults = Pick<NewUserProfile, 'id' | 'receiveEmailNotifications' | 'receivePushNotifications' | 'createdAt'>;

type UserProfileFormGroupContent = {
  id: FormControl<UserProfileFormRawValue['id'] | NewUserProfile['id']>;
  phone: FormControl<UserProfileFormRawValue['phone']>;
  avatarUrl: FormControl<UserProfileFormRawValue['avatarUrl']>;
  role: FormControl<UserProfileFormRawValue['role']>;
  preferredLanguage: FormControl<UserProfileFormRawValue['preferredLanguage']>;
  receiveEmailNotifications: FormControl<UserProfileFormRawValue['receiveEmailNotifications']>;
  receivePushNotifications: FormControl<UserProfileFormRawValue['receivePushNotifications']>;
  loyaltyPoints: FormControl<UserProfileFormRawValue['loyaltyPoints']>;
  createdAt: FormControl<UserProfileFormRawValue['createdAt']>;
  user: FormControl<UserProfileFormRawValue['user']>;
  location: FormControl<UserProfileFormRawValue['location']>;
};

export type UserProfileFormGroup = FormGroup<UserProfileFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class UserProfileFormService {
  createUserProfileFormGroup(userProfile: UserProfileFormGroupInput = { id: null }): UserProfileFormGroup {
    const userProfileRawValue = this.convertUserProfileToUserProfileRawValue({
      ...this.getFormDefaults(),
      ...userProfile,
    });
    return new FormGroup<UserProfileFormGroupContent>({
      id: new FormControl(
        { value: userProfileRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      phone: new FormControl(userProfileRawValue.phone, {
        validators: [Validators.maxLength(20)],
      }),
      avatarUrl: new FormControl(userProfileRawValue.avatarUrl, {
        validators: [Validators.maxLength(500)],
      }),
      role: new FormControl(userProfileRawValue.role, {
        validators: [Validators.required],
      }),
      preferredLanguage: new FormControl(userProfileRawValue.preferredLanguage, {
        validators: [Validators.maxLength(10)],
      }),
      receiveEmailNotifications: new FormControl(userProfileRawValue.receiveEmailNotifications),
      receivePushNotifications: new FormControl(userProfileRawValue.receivePushNotifications),
      loyaltyPoints: new FormControl(userProfileRawValue.loyaltyPoints, {
        validators: [Validators.min(0)],
      }),
      createdAt: new FormControl(userProfileRawValue.createdAt, {
        validators: [Validators.required],
      }),
      user: new FormControl(userProfileRawValue.user),
      location: new FormControl(userProfileRawValue.location),
    });
  }

  getUserProfile(form: UserProfileFormGroup): IUserProfile | NewUserProfile {
    return this.convertUserProfileRawValueToUserProfile(form.getRawValue() as UserProfileFormRawValue | NewUserProfileFormRawValue);
  }

  resetForm(form: UserProfileFormGroup, userProfile: UserProfileFormGroupInput): void {
    const userProfileRawValue = this.convertUserProfileToUserProfileRawValue({ ...this.getFormDefaults(), ...userProfile });
    form.reset(
      {
        ...userProfileRawValue,
        id: { value: userProfileRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): UserProfileFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      receiveEmailNotifications: false,
      receivePushNotifications: false,
      createdAt: currentTime,
    };
  }

  private convertUserProfileRawValueToUserProfile(
    rawUserProfile: UserProfileFormRawValue | NewUserProfileFormRawValue,
  ): IUserProfile | NewUserProfile {
    return {
      ...rawUserProfile,
      createdAt: dayjs(rawUserProfile.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertUserProfileToUserProfileRawValue(
    userProfile: IUserProfile | (Partial<NewUserProfile> & UserProfileFormDefaults),
  ): UserProfileFormRawValue | PartialWithRequiredKeyOf<NewUserProfileFormRawValue> {
    return {
      ...userProfile,
      createdAt: userProfile.createdAt ? userProfile.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
