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

  isHelper = false;

  ngOnInit(): void {
    if (localStorage.getItem("role") == "manager") {
      this.isHelper = false;
    } else if (localStorage.getItem("role") == "helper") {
      this.isHelper = true;
    }
  }

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

  changeUsername(): void {
    // verify a users role then do:
    if (localStorage.getItem("role") == "manager") {
      alert("You are not authorized to change the name of this account");
      return;
    }
    let id = +(localStorage.getItem('id') ?? '');
    let username = prompt("Enter New Username", "johndoe");
    if (username == null || username == '') {
      return;
    }
    this.accountsService.changeName(localStorage.getItem('id') ?? '', username, localStorage.getItem('key') || '')
      .subscribe({
        next: () => {
          alert("Username changed successfully");
          this.logout();
        },
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
              alert("Unknown error, is server online?");
          }
        }
      });
  }

  changePassword(): void {
    // verify a users role then do:
    let password = prompt("Enter New Password", "password");
    if (password == null || password == '') {
      return;
    }
    let response = confirm("Are you sure you want to change your password?");
    if (response) {
      this.accountsService.changePass(localStorage.getItem('id') ?? '', password, localStorage.getItem('key') || '')
        .subscribe({
          next: () => {
            alert("Password changed successfully");
            this.logout();
          },
          error: error => {
            switch (error.status) {
              case 401:
                alert("You are not authorized to change your password");
                break;
              case 403:
                alert("You are not allowed to change your password");
                break;
              case 500:
                alert("Internal server error\nPlease try again later!");
                break;
              default:
                alert("Unknown error, is server online?");
            }
          }
        });
    }
  }

  deleteUser(): void {
    // verify a user's role then:
    if (localStorage.getItem("role") == "manager") {
      alert("You are not authorized to delete this account");
      return;
    }
    let result = confirm("Are you sure you want to delete your account?");
    if (result) {
      this.accountsService.deleteUser(localStorage.getItem('id') || '', localStorage.getItem('key') || '')
        .subscribe({
          next: () => {
            alert("Deleted User");
            this.logout();
          },
          error: error => {
            switch (error.status) {
              case 401:
                alert("You are not authorized to delete your account");
                break;
              case 403:
                alert("You are not allowed to delete your account");
                break;
              case 500:
                alert("Internal server error\nPlease try again later!");
                break;
              default:
                alert("Unknown error, is server online?");
            }
          }
        });
    }
  }
}
