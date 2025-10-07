import { Component, Input } from '@angular/core';
import { AccountsService } from '../accountservice'

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
  
  constructor(private accountsService: AccountsService) {}

  login(): void {
    if(this.username == null || this.password == null) {
      return; // todo: more constructive erroring
    }
    const saved_username = this.username; // so you log in as the user you logged into, not what happened to be entered when it got processed
    this.accountsService.login(this.username, this.password)
      .subscribe(data => (this.finalizeLogin(data, saved_username)));
  }

  finalizeLogin(data: string, username: string): void {
    localStorage.setItem('key', data);
    localStorage.setItem('username', username);
    localStorage.setItem('role', username == 'admin' ? 'manager' : 'helper');
    // todo: switch page to cupboard, maybe?
  }
}
