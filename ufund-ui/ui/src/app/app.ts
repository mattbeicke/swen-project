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

  private account_paths = ['cupboard', 'accounts', 'basket'];

  /**
   * Checks on every page if the current session is still valid. 
   * If it's not, logs the user out.
   */
  ngOnInit(): void {
    if (localStorage.getItem("username") == null ||
      localStorage.getItem("username")?.length == 0) {
      // if username is empty or doesn't exist, return to login if user is on an account-only page

      for (var path in this.account_paths) {
        if (location.pathname.includes(this.account_paths[path])) {
          // URL includes name of account-only page
          this.deleteLogin();
        }
      }
      return;
    }
    this.accountsService.test(localStorage.getItem('username') ?? '', localStorage.getItem('key') ?? '').subscribe({
      next: () => { },
      error: () => {
        // Either API key is invalid or the server is down. Either way, force a log out
        this.deleteLogin();
      },
      complete: () => { this.verifyInfo() }
    })
  }

  /**
   * Verifies that the current saved data is accurate to the server-side data.
   */
  verifyInfo(): void {
    this.accountsService.getInfo(localStorage.getItem('username') ?? '', localStorage.getItem('key') ?? '').subscribe({
      next: (data) => {
        if (`${data.id}` != localStorage.getItem("id")
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

  /**
   * Removes the user's login data from localStorage
   */
  deleteLogin(): void {
    localStorage.setItem("username", "");
    localStorage.setItem("key", "");
    localStorage.setItem("role", "");
    localStorage.setItem("id", "");

    this.router.navigate(['/']);
  }
}
