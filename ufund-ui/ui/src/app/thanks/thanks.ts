import { Component } from '@angular/core';
import { CompletedService } from '../completedservice';
import { Router } from '@angular/router';
import { CompletedNeed } from '../completedneed';

@Component({
  selector: 'app-thanks',
  standalone: false,
  templateUrl: './thanks.html',
  styleUrl: './thanks.css'
})
export class Thanks {
  constructor(private completedService: CompletedService, private router: Router) { }

  completedNeeds$: CompletedNeed[] = [];
  page: number = 1;

  day?: number = 0;
  week?: number = 0;
  month?: number = 0;
  year?: number = 0;

  ngOnInit(): void {
    this.completedService.getCompletedNeedsPage(this.page)
      .subscribe(completed => this.completedNeeds$ = completed);

    this.completedService.getNumbers().subscribe({
      next: nums => {
        this.day = nums[0];
        this.week = nums[1];
        this.month = nums[2];
        this.year = nums[3];
      }
    });
  }

  shiftPage(n: number) {
    this.page += n;
    if (this.page <= 0) {
      this.page = 1;
      return;
    }
    this.completedService.getCompletedNeedsPage(this.page)
      .subscribe(completed => {
        if (completed.length == 0) {
          this.page -= 1;
          return;
        }
        this.completedNeeds$ = completed;
      });
  }

  padInt(n: number) {
    return ((n < 10) ? "0" : "") + n;
  }

  toDate(millisecond: number): string {
    let date = new Date(millisecond * 1000); // JS takes in millisecond, API returns seconds
    let format = `${date.getMonth() + 1}/${this.padInt(date.getDate())}/${date.getFullYear()} at ${date.getHours() % 12}:${this.padInt(date.getMinutes())} ${date.getHours() >= 12 ? "PM" : "AM"}`
    return format;
  }
}
