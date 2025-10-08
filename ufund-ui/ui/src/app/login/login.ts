import { Component, Input, OnInit } from '@angular/core';
import { AccountsService } from '../accountservice'
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login implements OnInit {

  @Input() username?: string;
  @Input() password?: string;
  key?: string;
  message?: string;

  constructor(private accountsService: AccountsService, private router: Router) { }

  ngOnInit(): void {
    if (localStorage.getItem('username')) { // username is present => force redirect to cupboard 
      this.router.navigate(['/cupboard']);
    }
  }

  login(): void {
    if (!this.username) { // missing or removed username
      this.message = "Missing username"
      return;
    }
    if (!this.password) { // missing or removed password
      this.message = "Missing password"
      return;
    }
    const saved_username = this.username; // so you log in as the user you logged into, not what happened to be entered when it got processed
    this.accountsService.login(this.username, this.password)
      .subscribe({
        next: data => (this.finalizeLogin(data, saved_username)),
        error: error => {
          switch (error.status) {
            case 401:
              this.message = "Invalid username or password";
              break;
            case 500:
              this.message = "Internal server error";
              break;
            default:
              this.message = "Unknown error, is server online?";
          }
        }
      });
  }

  finalizeLogin(key: string, username: string): void {
    localStorage.setItem('key', key);
    localStorage.setItem('username', username);
    localStorage.setItem('role', username == 'admin' ? 'manager' : 'helper');

    // Now that the login is successful, find the user's ID
    this.accountsService.getInfo(username, key)
      .subscribe({
        next: data => {
          const id: number = JSON.parse(data).id
          localStorage.setItem('id', `${id}`);
          this.router.navigate(['/cupboard']);
        },
        error: error => {
          switch (error.status) {
            case 401:
              this.message = "Authorization successful, but could not find account"; // This should never happen.
              break;
            case 500:
              this.message = "Internal server error";
              break;
            default:
              this.message = "Unknown error, is server online?";
          }
        }
      });

  }


}
