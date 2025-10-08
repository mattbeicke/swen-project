import { Component, Input } from '@angular/core';
import { AccountsService } from '../accountservice'
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  @Input() username?: string;
  @Input() password?: string;
  key?: string;
  message?: string;

  constructor(private accountsService: AccountsService, private router: Router) { }

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
        error: error => {switch(error.status){
          case 401:
            this.message = "Invalid username or password";
            break;
          case 500:
            this.message = "Internal server error";
            break;
          default:
            this.message = "Unknown error, is server online?";
        }}
      });
  }

  finalizeLogin(data: string, username: string): void {
    localStorage.setItem('key', "");
    localStorage.setItem('username', username);
    localStorage.setItem('role', username == 'admin' ? 'manager' : 'helper');

    this.router.navigate(['/cupboard']);
  }

  
}
