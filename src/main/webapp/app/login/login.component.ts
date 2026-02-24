import { AfterViewInit, Component, ElementRef, OnInit, inject, signal, viewChild } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

import SharedModule from 'app/shared/shared.module';
import { LoginService } from 'app/login/login.service';
import { AccountService } from 'app/core/auth/account.service';
import { RegisterService } from 'app/account/register/register.service';
import { EMAIL_ALREADY_USED_TYPE, LOGIN_ALREADY_USED_TYPE } from 'app/config/error.constants';

@Component({
  selector: 'jhi-login',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export default class LoginComponent implements OnInit, AfterViewInit {
  usernameRef = viewChild<ElementRef>('usernameRef');

  isLogin = signal(true);
  showPassword = signal(false);

  // Login signals
  authenticationError = signal(false);

  // Register signals
  doNotMatch = signal(false);
  errorRegister = signal(false);
  errorEmailExists = signal(false);
  errorUserExists = signal(false);
  registerSuccess = signal(false);

  loginForm = new FormGroup({
    username: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    password: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    rememberMe: new FormControl(false, { nonNullable: true }),
  });

  registerForm = new FormGroup({
    login: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(50),
        Validators.pattern('^[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^[_.@A-Za-z0-9-]+$'),
      ],
    }),
    email: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(5), Validators.maxLength(254), Validators.email],
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(4), Validators.maxLength(50)],
    }),
    confirmPassword: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(4), Validators.maxLength(50)],
    }),
  });

  private readonly accountService = inject(AccountService);
  private readonly loginService = inject(LoginService);
  private readonly registerService = inject(RegisterService);
  private readonly translateService = inject(TranslateService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  ngOnInit(): void {
    this.accountService.identity().subscribe(() => {
      if (this.accountService.isAuthenticated()) {
        this.router.navigate(['']);
      }
    });

    this.route.queryParamMap.subscribe(params => {
      if (params.get('mode') === 'register') {
        this.isLogin.set(false);
      }
    });
  }

  ngAfterViewInit(): void {
    this.usernameRef()?.nativeElement.focus();
  }

  login(): void {
    this.authenticationError.set(false);
    this.loginService.login(this.loginForm.getRawValue()).subscribe({
      next: () => {
        if (!this.router.getCurrentNavigation()) {
          this.router.navigate(['']);
        }
      },
      error: () => this.authenticationError.set(true),
    });
  }

  register(): void {
    this.doNotMatch.set(false);
    this.errorRegister.set(false);
    this.errorEmailExists.set(false);
    this.errorUserExists.set(false);

    const { password, confirmPassword } = this.registerForm.getRawValue();
    if (password !== confirmPassword) {
      this.doNotMatch.set(true);
      return;
    }

    const { login, email } = this.registerForm.getRawValue();
    this.registerService.save({ login, email, password, langKey: this.translateService.currentLang }).subscribe({
      next: () => {
        this.registerSuccess.set(true);
        setTimeout(() => {
          this.isLogin.set(true);
          this.registerSuccess.set(false);
          this.registerForm.reset();
        }, 2000);
      },
      error: (response: HttpErrorResponse) => this.processRegisterError(response),
    });
  }

  togglePassword(): void {
    this.showPassword.update(v => !v);
  }

  toggleMode(): void {
    this.isLogin.update(v => !v);
    this.authenticationError.set(false);
    this.doNotMatch.set(false);
    this.errorRegister.set(false);
    this.errorEmailExists.set(false);
    this.errorUserExists.set(false);
    this.registerSuccess.set(false);
  }

  private processRegisterError(response: HttpErrorResponse): void {
    if (response.status === 400 && response.error.type === LOGIN_ALREADY_USED_TYPE) {
      this.errorUserExists.set(true);
    } else if (response.status === 400 && response.error.type === EMAIL_ALREADY_USED_TYPE) {
      this.errorEmailExists.set(true);
    } else {
      this.errorRegister.set(true);
    }
  }
}
