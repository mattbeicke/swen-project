import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { Cupboard } from './cupboard/cupboard';
import { Login } from './login/login';
import { Account } from './account/account';
import { BasketTab } from './basket-tab/basket-tab';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [
    App,
    Cupboard,
    Login,
    Account,
    BasketTab
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [
    provideBrowserGlobalErrorListeners()
  ],
  bootstrap: [App]
})
export class AppModule { }
