import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IReview, NewReview } from '../review.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IReview for edit and NewReviewFormGroupInput for create.
 */
type ReviewFormGroupInput = IReview | PartialWithRequiredKeyOf<NewReview>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IReview | NewReview> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

type ReviewFormRawValue = FormValueOf<IReview>;

type NewReviewFormRawValue = FormValueOf<NewReview>;

type ReviewFormDefaults = Pick<NewReview, 'id' | 'isApproved' | 'isAnonymous' | 'createdAt'>;

type ReviewFormGroupContent = {
  id: FormControl<ReviewFormRawValue['id'] | NewReview['id']>;
  overallRating: FormControl<ReviewFormRawValue['overallRating']>;
  foodRating: FormControl<ReviewFormRawValue['foodRating']>;
  serviceRating: FormControl<ReviewFormRawValue['serviceRating']>;
  ambienceRating: FormControl<ReviewFormRawValue['ambienceRating']>;
  comment: FormControl<ReviewFormRawValue['comment']>;
  isApproved: FormControl<ReviewFormRawValue['isApproved']>;
  isAnonymous: FormControl<ReviewFormRawValue['isAnonymous']>;
  createdAt: FormControl<ReviewFormRawValue['createdAt']>;
  location: FormControl<ReviewFormRawValue['location']>;
  reservation: FormControl<ReviewFormRawValue['reservation']>;
  client: FormControl<ReviewFormRawValue['client']>;
};

export type ReviewFormGroup = FormGroup<ReviewFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ReviewFormService {
  createReviewFormGroup(review: ReviewFormGroupInput = { id: null }): ReviewFormGroup {
    const reviewRawValue = this.convertReviewToReviewRawValue({
      ...this.getFormDefaults(),
      ...review,
    });
    return new FormGroup<ReviewFormGroupContent>({
      id: new FormControl(
        { value: reviewRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      overallRating: new FormControl(reviewRawValue.overallRating, {
        validators: [Validators.required, Validators.min(1), Validators.max(5)],
      }),
      foodRating: new FormControl(reviewRawValue.foodRating, {
        validators: [Validators.min(1), Validators.max(5)],
      }),
      serviceRating: new FormControl(reviewRawValue.serviceRating, {
        validators: [Validators.min(1), Validators.max(5)],
      }),
      ambienceRating: new FormControl(reviewRawValue.ambienceRating, {
        validators: [Validators.min(1), Validators.max(5)],
      }),
      comment: new FormControl(reviewRawValue.comment),
      isApproved: new FormControl(reviewRawValue.isApproved, {
        validators: [Validators.required],
      }),
      isAnonymous: new FormControl(reviewRawValue.isAnonymous, {
        validators: [Validators.required],
      }),
      createdAt: new FormControl(reviewRawValue.createdAt, {
        validators: [Validators.required],
      }),
      location: new FormControl(reviewRawValue.location),
      reservation: new FormControl(reviewRawValue.reservation),
      client: new FormControl(reviewRawValue.client),
    });
  }

  getReview(form: ReviewFormGroup): IReview | NewReview {
    return this.convertReviewRawValueToReview(form.getRawValue() as ReviewFormRawValue | NewReviewFormRawValue);
  }

  resetForm(form: ReviewFormGroup, review: ReviewFormGroupInput): void {
    const reviewRawValue = this.convertReviewToReviewRawValue({ ...this.getFormDefaults(), ...review });
    form.reset(
      {
        ...reviewRawValue,
        id: { value: reviewRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ReviewFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      isApproved: false,
      isAnonymous: false,
      createdAt: currentTime,
    };
  }

  private convertReviewRawValueToReview(rawReview: ReviewFormRawValue | NewReviewFormRawValue): IReview | NewReview {
    return {
      ...rawReview,
      createdAt: dayjs(rawReview.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertReviewToReviewRawValue(
    review: IReview | (Partial<NewReview> & ReviewFormDefaults),
  ): ReviewFormRawValue | PartialWithRequiredKeyOf<NewReviewFormRawValue> {
    return {
      ...review,
      createdAt: review.createdAt ? review.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
