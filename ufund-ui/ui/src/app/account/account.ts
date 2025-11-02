import { Component, Input } from '@angular/core';
import { AccountsService } from '../accountservice';
import { Router } from '@angular/router';

/**
 * Code behind the Accounts Tab
 * @author Ricardo Lopez
 */

@Component({
  selector: 'app-account',
  standalone: false,
  templateUrl: './account.html',
  styleUrl: './account.css'
})
export class Account {
  constructor(private accountsService: AccountsService, private router: Router) { }

  @Input() password? : string;
  @Input() username? : string;
  showPassBox = false;
  showNameBox = false;
  isHelper = false;

  ngOnInit(): void {
    if (localStorage.getItem("role") == "manager") {
      this.isHelper = false;
    } else if (localStorage.getItem("role") == "helper") {
      this.isHelper = true;
    }
  }

  /**
   * On confirmation, logs the user out
   */
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
  /**
   * Checks if user is a helper, Calls account service for changing username, reports errors, then logs the user out
   */
  changeUsername(): void {
    // verify a users role then do:
    if (localStorage.getItem("role") == "manager") {
      alert("You are not authorized to change the name of this account");
      return;
    }
    this.username = this.username?.trim()
    if (!this.username) {
      return;
    }

    this.accountsService.changeName(localStorage.getItem('id') ?? '', this.username, localStorage.getItem('key') || '')
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

  /**
   * Calls account service for changing account password, reports errors, then logs the user out
   */
  changePassword(): void {
    // verify a users role then do:
    this.password = this.password?.trim()

    if (!this.password){
      return
    }

    this.accountsService.changePass(localStorage.getItem('id') ?? '', this.password, localStorage.getItem('key') || '')
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

  /**
   * Checks if user isn't a manager, after a confirmation, user account is deleted
   */
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

  flipPass() {
    this.showPassBox = true
  }

  flipUser() {
    this.showNameBox = true
  }
}
