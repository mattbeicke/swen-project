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

  max?: number;
  n?: number;
  display?: number;

  users!: User[];

  ngOnInit(): void {
    this.userService.getMaxUsers().subscribe({
      next: max => {
        this.max = max;
        if (max < 5) {
          this.n = max;
          this.display = max;
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
    else if (n > this.max!) n = this.max!;

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

  /**
   * locks down the input values for setting max amount of people you can see
   * 
   * @param event what was entered
   */
  preventInvalid(event: KeyboardEvent): void {
    const allowed = ['Backspace', 'ArrowLeft', 'ArrowRight', 'Delete', 'Tab'];
    if (allowed.includes(event.key)) return;

    const nextValue = (event.target as HTMLInputElement).value + event.key;
    const num = parseInt(nextValue, 10);

    if (isNaN(num) || num < 1 || num > this.max!) {
      event.preventDefault();
    }
  }
}