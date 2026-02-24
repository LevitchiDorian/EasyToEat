import { Routes } from '@angular/router';

import { Authority } from 'app/config/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { errorRoute } from './layouts/error/error.route';

const routes: Routes = [
  // ── Public landing page ──────────────────────────────────────────────────
  {
    path: '',
    loadComponent: () => import('./home/home.component'),
    title: 'ReservoAI — Rezervă masa perfectă',
  },

  // ── Navbar (named outlet) ────────────────────────────────────────────────
  {
    path: '',
    loadComponent: () => import('./layouts/navbar/navbar.component'),
    outlet: 'navbar',
  },

  // ── Public restaurant pages ──────────────────────────────────────────────
  {
    path: 'restaurante',
    loadComponent: () => import('./public/restaurants/restaurants.component'),
    title: 'Descoperă restaurante — ReservoAI',
  },
  {
    path: 'restaurante/:id',
    loadComponent: () => import('./public/restaurant-detail/restaurant-detail.component'),
    title: 'Restaurant — ReservoAI',
  },

  // ── Placeholder public pages ─────────────────────────────────────────────
  {
    path: 'cum-functioneaza',
    loadComponent: () => import('./home/home.component'),
    title: 'Cum funcționează — ReservoAI',
  },
  {
    path: 'pentru-afaceri',
    loadComponent: () => import('./home/home.component'),
    title: 'Pentru afaceri — ReservoAI',
  },

  // ── Admin section ────────────────────────────────────────────────────────
  {
    path: 'admin',
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./admin/admin.routes'),
  },

  // ── Account section ──────────────────────────────────────────────────────
  {
    path: 'account',
    loadChildren: () => import('./account/account.route'),
  },

  // ── Login ────────────────────────────────────────────────────────────────
  {
    path: 'login',
    loadComponent: () => import('./login/login.component'),
    title: 'Autentificare — ReservoAI',
  },

  // ── Profile ──────────────────────────────────────────────────────────────
  {
    path: 'profil',
    loadComponent: () => import('./public/profile/profile.component'),
    canActivate: [UserRouteAccessService],
    data: { authorities: [Authority.USER] },
    title: 'Profilul meu — ReservoAI',
  },

  // ── JHipster entity CRUD routes ──────────────────────────────────────────
  {
    path: '',
    loadChildren: () => import('./entities/entity.routes'),
  },

  ...errorRoute,
];

export default routes;
