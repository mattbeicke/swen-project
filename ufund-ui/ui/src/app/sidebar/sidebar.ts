import { Component } from '@angular/core';
import { AccountsService } from '../accountservice';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: false,
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css'
})
export class Sidebar {
  constructor(private accountsService: AccountsService, private router: Router) { }

  isManager?: boolean;
  ngOnInit(): void {
    if (localStorage.getItem('role') == 'manager') {
      this.isManager = true;
    } else {
      this.isManager = false;
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
}
