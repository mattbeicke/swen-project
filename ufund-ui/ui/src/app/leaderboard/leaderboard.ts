import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../user';
import { UserService } from '../userservice';

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

  users!: User[];

  ngOnInit(): void {
    this.userService.getMaxUsers().subscribe({
      next: max => {
        this.max = max;
        if (max < 5) {
          this.n = max;
        } else {
          this.n = 5;
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

  getTop(n: number): void {
    if (n < 1 || n > this.max!) {
      this.n = 5;
    } else {
      this.n = n;
    }
    this.userService.getTopNUsers(this.n).subscribe({
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