import { Component, OnInit, inject, signal } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';

@Component({
  selector: 'jhi-profile',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
})
export default class ProfileComponent implements OnInit {
  account = signal<Account | null>(null);
  isEditing = signal(false);
  isSaving = signal(false);
  success = signal(false);
  error = signal(false);

  profileForm = new FormGroup({
    firstName: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(1), Validators.maxLength(50)],
    }),
    lastName: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(1), Validators.maxLength(50)],
    }),
    email: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.email, Validators.maxLength(254)],
    }),
  });

  private readonly accountService = inject(AccountService);
  private readonly router = inject(Router);

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => {
      if (!account) {
        this.router.navigate(['/login']);
        return;
      }
      this.account.set(account);
      this.profileForm.patchValue({
        firstName: account.firstName ?? '',
        lastName: account.lastName ?? '',
        email: account.email,
      });
    });
  }

  startEditing(): void {
    const acc = this.account();
    if (acc) {
      this.profileForm.patchValue({
        firstName: acc.firstName ?? '',
        lastName: acc.lastName ?? '',
        email: acc.email,
      });
    }
    this.isEditing.set(true);
    this.success.set(false);
    this.error.set(false);
  }

  cancelEditing(): void {
    this.isEditing.set(false);
    this.error.set(false);
  }

  save(): void {
    const acc = this.account();
    if (!acc || this.profileForm.invalid) return;

    this.isSaving.set(true);
    this.success.set(false);
    this.error.set(false);

    const { firstName, lastName, email } = this.profileForm.getRawValue();
    const updated: Account = { ...acc, firstName, lastName, email };

    this.accountService.save(updated).subscribe({
      next: () => {
        this.accountService.authenticate(updated);
        this.account.set(updated);
        this.isEditing.set(false);
        this.isSaving.set(false);
        this.success.set(true);
      },
      error: () => {
        this.error.set(true);
        this.isSaving.set(false);
      },
    });
  }

  getInitials(): string {
    const acc = this.account();
    if (!acc) return 'U';
    const name = `${acc.firstName ?? ''} ${acc.lastName ?? ''}`.trim() || acc.login || acc.email;
    return name
      .split(' ')
      .map(n => n[0])
      .join('')
      .toUpperCase()
      .slice(0, 2);
  }
}
