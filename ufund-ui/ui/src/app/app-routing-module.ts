import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Cupboard } from './cupboard/cupboard';
import { Login } from './login/login';
import { Account } from './account/account';
import { BasketTab } from './basket-tab/basket-tab';

const routes: Routes = [
  { path: 'cupboard', component: Cupboard },
  { path: 'login', component: Login },
  { path: 'account', component: Account },
  { path: 'baskettab', component: BasketTab },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }