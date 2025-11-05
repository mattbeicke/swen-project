import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { NeedService } from '../needservice';
import { CompletedService } from '../completedservice';
import { CompletedNeed } from '../completedneed';

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home {
  constructor(private completedService: CompletedService, private router: Router) { }

    recents: CompletedNeed[] = [];


  padInt(n: number) {
    return ((n < 10) ? "0" : "") + n;
  }

  toDate(millisecond: number): string {
    let date = new Date(millisecond*1000); // JS takes in millisecond, API returns seconds
    let format = `${date.getMonth()+1}/${this.padInt(date.getDate())}/${date.getFullYear()} at ${date.getHours()%12}:${this.padInt(date.getMinutes())} ${date.getHours() >= 12 ? "PM" : "AM"}`
    return format;
  }
  
  ngOnInit(): void {
    if (localStorage.getItem('username')) { // username is present => force redirect to cupboard 
      this.router.navigate(['/cupboard']);
    }
    this.completedService.getCompletedNeedsPage(1)
      .subscribe(completed => this.recents = completed.slice(0, 5));
  }

  login(): void {
    this.router.navigate(['/login']);
  }
}
