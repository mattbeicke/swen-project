import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { NeedService } from '../needservice';

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home {
  constructor(private needService: NeedService, private router: Router) { }

  recent?: string;

  ngOnInit(): void {
    if (localStorage.getItem('username')) { // username is present => force redirect to cupboard 
      this.router.navigate(['/cupboard']);
    }

    //get 5 most recent contributions and format them and display them
  }

  login(): void {
    this.router.navigate(['/login']);
  }
}
