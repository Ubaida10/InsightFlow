import { Routes } from '@angular/router';
import {authGuard} from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () => import('../app/components/auth/login/login')
      .then(m => m.Login)
  },
  {
    path: 'register',
    loadComponent: () => import('../app/components/auth/register/register')
      .then(m => m.Register)
  },
  {
    path: 'dashboard',
    loadComponent: () => import('../app/components/dashboard/dashboard')
      .then(m => m.Dashboard),
    canActivate: [authGuard]
  },
  {
    path: 'trends/:testName',
    loadComponent: ()=> import ('./components/trend-chart/trend-chart')
      .then(m=>m.TrendChart),
    canActivate: [authGuard]
  },
  { path: '**', redirectTo: 'login' }
];
