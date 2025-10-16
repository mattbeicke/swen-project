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
    if (localStorage.getItem("role") != "manager") {
      alert("You are not authorized to create new Needs");
      return;
    } else if (localStorage.getItem("role") == "helper"){
      this.accountsService.changeName(name, localStorage.getItem('key') || '').subscribe({next: name => (name)});
    }
  }
}
