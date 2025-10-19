import { Component } from '@angular/core';
import { AccountsService } from './accountservice';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  standalone: false,
  styleUrl: './app.css'
})
export class App {
  constructor(private accountsService: AccountsService, private router: Router) { }

  ngOnInit(): void {
    if(localStorage.getItem("username") == null || 
      localStorage.getItem("username")?.length == 0) { return; } // if username is empty or doesn't exist, ignore this code
    this.accountsService.test(localStorage.getItem('username') ?? '', localStorage.getItem('key') ?? '').subscribe({
      next: () => {},
      error: () => {
        // Either API key is invalid or the server is down. Either way, force a log out
        this.deleteLogin();
      },
      complete: () => {this.verifyInfo()}
    })
  }

  verifyInfo(): void {
    this.accountsService.getInfo(localStorage.getItem('username') ?? '', localStorage.getItem('key') ?? '').subscribe({
      next: (data) => {
        if(`${data.id}` != localStorage.getItem("id")
          || (data.manager ? 'manager' : 'helper') != localStorage.getItem("role")) {
            // ID or role is wrong, clearly the credentials are wrong -> delete login
            this.deleteLogin();
          }
      },
      error: () => {
        // Again, either API key is invalid or the server is down. Either way, force a log out
        this.deleteLogin();
      }
    })
  }

  deleteLogin(): void {
    localStorage.setItem("username", "");
    localStorage.setItem("key", "");
    localStorage.setItem("role", "");
    localStorage.setItem("id", "");

    this.router.navigate(['/']);
  }
}
