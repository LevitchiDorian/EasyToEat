import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'restaurantApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'brand',
    data: { pageTitle: 'restaurantApp.brand.home.title' },
    loadChildren: () => import('./brand/brand.routes'),
  },
  {
    path: 'location',
    data: { pageTitle: 'restaurantApp.location.home.title' },
    loadChildren: () => import('./location/location.routes'),
  },
  {
    path: 'location-hours',
    data: { pageTitle: 'restaurantApp.locationHours.home.title' },
    loadChildren: () => import('./location-hours/location-hours.routes'),
  },
  {
    path: 'dining-room',
    data: { pageTitle: 'restaurantApp.diningRoom.home.title' },
    loadChildren: () => import('./dining-room/dining-room.routes'),
  },
  {
    path: 'restaurant-table',
    data: { pageTitle: 'restaurantApp.restaurantTable.home.title' },
    loadChildren: () => import('./restaurant-table/restaurant-table.routes'),
  },
  {
    path: 'user-profile',
    data: { pageTitle: 'restaurantApp.userProfile.home.title' },
    loadChildren: () => import('./user-profile/user-profile.routes'),
  },
  {
    path: 'menu-category',
    data: { pageTitle: 'restaurantApp.menuCategory.home.title' },
    loadChildren: () => import('./menu-category/menu-category.routes'),
  },
  {
    path: 'menu-item',
    data: { pageTitle: 'restaurantApp.menuItem.home.title' },
    loadChildren: () => import('./menu-item/menu-item.routes'),
  },
  {
    path: 'location-menu-item-override',
    data: { pageTitle: 'restaurantApp.locationMenuItemOverride.home.title' },
    loadChildren: () => import('./location-menu-item-override/location-menu-item-override.routes'),
  },
  {
    path: 'menu-item-allergen',
    data: { pageTitle: 'restaurantApp.menuItemAllergen.home.title' },
    loadChildren: () => import('./menu-item-allergen/menu-item-allergen.routes'),
  },
  {
    path: 'menu-item-option',
    data: { pageTitle: 'restaurantApp.menuItemOption.home.title' },
    loadChildren: () => import('./menu-item-option/menu-item-option.routes'),
  },
  {
    path: 'menu-item-option-value',
    data: { pageTitle: 'restaurantApp.menuItemOptionValue.home.title' },
    loadChildren: () => import('./menu-item-option-value/menu-item-option-value.routes'),
  },
  {
    path: 'reservation',
    data: { pageTitle: 'restaurantApp.reservation.home.title' },
    loadChildren: () => import('./reservation/reservation.routes'),
  },
  {
    path: 'reservation-table',
    data: { pageTitle: 'restaurantApp.reservationTable.home.title' },
    loadChildren: () => import('./reservation-table/reservation-table.routes'),
  },
  {
    path: 'waiting-list',
    data: { pageTitle: 'restaurantApp.waitingList.home.title' },
    loadChildren: () => import('./waiting-list/waiting-list.routes'),
  },
  {
    path: 'restaurant-order',
    data: { pageTitle: 'restaurantApp.restaurantOrder.home.title' },
    loadChildren: () => import('./restaurant-order/restaurant-order.routes'),
  },
  {
    path: 'order-item',
    data: { pageTitle: 'restaurantApp.orderItem.home.title' },
    loadChildren: () => import('./order-item/order-item.routes'),
  },
  {
    path: 'order-item-option-selection',
    data: { pageTitle: 'restaurantApp.orderItemOptionSelection.home.title' },
    loadChildren: () => import('./order-item-option-selection/order-item-option-selection.routes'),
  },
  {
    path: 'payment',
    data: { pageTitle: 'restaurantApp.payment.home.title' },
    loadChildren: () => import('./payment/payment.routes'),
  },
  {
    path: 'review',
    data: { pageTitle: 'restaurantApp.review.home.title' },
    loadChildren: () => import('./review/review.routes'),
  },
  {
    path: 'notification',
    data: { pageTitle: 'restaurantApp.notification.home.title' },
    loadChildren: () => import('./notification/notification.routes'),
  },
  {
    path: 'promotion',
    data: { pageTitle: 'restaurantApp.promotion.home.title' },
    loadChildren: () => import('./promotion/promotion.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
