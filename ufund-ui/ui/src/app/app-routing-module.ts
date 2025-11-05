import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Cupboard } from './cupboard/cupboard';
import { Login } from './login/login';
import { Account } from './account/account';
import { BasketTab } from './basket-tab/basket-tab';
import { UsersTab } from './userstab/userstab';
import { ForgotPassword } from './forgotpassword/forgotpassword';
import { Home } from './home/home';
import { Thanks } from './thanks/thanks';
import { Leaderboard } from './leaderboard/leaderboard';

const routes: Routes = [
  { path: 'cupboard', component: Cupboard },
  { path: 'login', component: Login },
  { path: 'account', component: Account },
  { path: 'basket', component: BasketTab },
  { path: 'users', component: UsersTab },
  { path: 'thanks', component: Thanks },
  { path: 'forgotpassword', component: ForgotPassword },
  { path: 'leaderboard', component: Leaderboard },
  { path: 'home', component: Home },
  { path: '', redirectTo: '/home', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }