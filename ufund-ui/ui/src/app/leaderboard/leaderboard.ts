import { Component } from '@angular/core';
import { User } from '../user';
import { UserService } from '../userservice';


/**
 * Code behind the leaderboard tab
 *
 * @author Matthew Beicke
 */
@Component({
  selector: 'app-leaderboard',
  standalone: false,
  templateUrl: './leaderboard.html',
  styleUrl: './leaderboard.css'
})
export class Leaderboard {
  constructor(private userService: UserService) { }

  maxn?: number;
  n?: number;
  display?: number;

  users!: User[];

  ngOnInit(): void {
    this.userService.getMaxUsers().subscribe({
      next: maxn => {
        this.maxn = maxn;
        if (maxn < 5) {
          this.n = maxn;
          this.display = maxn;
        } else {
          this.n = 5;
          this.display = 5;
        }
        this.getTop(this.n);
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
  }

  /**
   * Gets the top n contributors
   * 
   * @param n number to limit by
   */
  getTop(n: number): void {
    if (!n) {
      return;
    }
    if (n < 1) n = 1;
    else if (n > this.maxn!) n = this.maxn!;

    this.n = n;
    this.display = n;

    this.userService.getTopNUsers(this.n!).subscribe({
      next: users => {
        this.users = users;
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
  }
}