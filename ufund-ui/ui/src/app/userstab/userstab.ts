import { Component } from '@angular/core';
import { UserService } from '../userservice';
import { Router } from '@angular/router';
import { Observable, startWith, Subject, switchMap } from 'rxjs';
import { User } from '../user';

/**
 * Code behind the Users tab
 * 
 * @author Matthew Beicke
 */
@Component({
  selector: 'app-userstab',
  standalone: false,
  templateUrl: './userstab.html',
  styleUrl: './userstab.css'
})
export class UsersTab {
  constructor(private userService: UserService, private router: Router) { }

  users!: Observable<User[]>;
  selectedUser?: User;

  private searchTerms = new Subject<string>();
  searchValue: string = "";

  /**
   * This code runs on initialization, verifying the accessing user is a manager and initially populating the list of users
   */
  ngOnInit(): void {
    if (localStorage.getItem("role") != "manager") {
      this.router.navigate(['/login']);
    }

    this.userService.getUsers(localStorage.getItem("key") ?? "").subscribe({
      next: users => {
        this.users = this.searchTerms.pipe(
          switchMap((term: string) => this.userService.searchUsers(term, localStorage.getItem("key") ?? "")),
          startWith(users)
        );
      },
      error: error => {
        switch (error.status) {
          case 401:
            alert("You are not authorized to see list of Users");
            this.router.navigate(['/login']);
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
   * This function will handle the banning/unbanning of a user
   * 
   * @param user User to ban/unban
   */
  onSelect(user: User): void {
    this.userService.toggleBan(user, localStorage.getItem("key") ?? "").subscribe({
      next: _ => {
        this.searchTerms.next(this.searchValue);
      },
      error: error => {
        switch (error.status) {
          case 500:
            alert("Internal server error\nPlease try again later!");
            break;
          default:
            alert("Unknown error, is server online?");
        }
      }
    });
    // ban/unban stuff here
  }

  /**
   * This function will handle searching for a user
   * 
   * @param term what to search for
   */
  search(term: string): void {
    this.searchTerms.next(term);
    this.searchValue = term;
  }
}
