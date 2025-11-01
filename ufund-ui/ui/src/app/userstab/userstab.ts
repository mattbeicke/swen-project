import { Component } from '@angular/core';
import { UserService } from '../userservice';
import { Router } from '@angular/router';
import { Observable, startWith, Subject, switchMap } from 'rxjs';
import { User } from '../user';

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

  onSelect(user: User): void {
    // ban/unban stuff here
  }

  search(term: string): void {
    this.searchTerms.next(term);
    this.searchValue = term;
  }

  ngOnInit(): void {
    if (localStorage.getItem("role") != "manager") {
      this.router.navigate(['/cupboard']);
    }

    this.userService.getUsers(localStorage.getItem("key") ?? "").subscribe(users => {
      this.users = this.searchTerms.pipe(
        switchMap((term: string) => this.userService.searchUsers(term, localStorage.getItem("key") ?? "")),
        startWith(users)
      );
    });
  }
}
