import { Component } from '@angular/core';
import { AccountsService } from '../accountservice';
import { Router } from '@angular/router';

@Component({
  selector: 'app-account',
  standalone: false,
  templateUrl: './account.html',
  styleUrl: './account.css'
})
export class Account {

  constructor(private accountsService: AccountsService, private router: Router) { }

  logout(): void {
    const username: string = localStorage.getItem('username') || '';
    const key: string = localStorage.getItem('key') || '';
    this.accountsService.logout(username, key);
    
    localStorage.setItem("username", "");
    localStorage.setItem("key", "");
    localStorage.setItem("role", "");
    localStorage.setItem("id", "");

    this.router.navigate(['/']);
  }

  changeUsername(name: string): void{
    // verify a users role then do:
    if (localStorage.getItem("role") == "manager") {
      alert("You are not authorized to change the name of this account");
      return;
    } else {
        this.accountsService.changeName(name, localStorage.getItem('key') || '')
          .subscribe({
            next: name => (name), 
            error: error => {
              switch (error.status) {
                case 401:
                  alert("You are not authorized to change this name");
                  break;
                case 403:
                  alert("You are not allowed to change this name");
                  break;
                case 500:
                  alert("Internal server error\nPlease try again later!");
                  break;
                default:
                  alert("Unknown error, is server online?");}
        }
      });
    }
  }

  changePassword(pass: string): void{
    // verify a users role then do:
    if (localStorage.getItem("role") == "helper"){
      this.accountsService.changePass(pass, localStorage.getItem('key') || '')
        .subscribe({
          next: pass => (pass),
          error: error => {
            switch (error.status) {
              case 401:
                alert("You are not authorized to change this password");
                break;
              case 403:
                alert("You are not allowed to change this password");
                break;
              case 500:
                alert("Internal server error\nPlease try again later!");
                break;
              default:
                alert("Unknown error, is server online?");}}});
    }
  }

  deleteUser(): void {
    this.accountsService.deleteUser(localStorage.getItem('id') || '', localStorage.getItem('key') || '')
      .subscribe({
        next: () => {alert("Deleted User")}, 
        error: error => {
          switch (error.status) {
            case 401:
              alert("You are not authorized to delete this user");
              break;
            case 403:
              alert("You are not allowed to delete this user");
              break;
            case 500:
              alert("Internal server error\nPlease try again later!");
              break;
            default:
              alert("Unknown error, is server online?");
          }
        }
    },)
  }
}
