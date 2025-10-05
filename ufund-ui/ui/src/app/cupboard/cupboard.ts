import { Component } from '@angular/core';
import { Need } from '../need';
import { NeedService } from '../needservice';

import {
  NgIf,
  NgFor,
  UpperCasePipe,
} from '@angular/common';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-cupboard',
  standalone: false,
  templateUrl: './cupboard.html',
  styleUrl: './cupboard.css'
})
export class Cupboard {
  selectedNeed?: Need;

  onSelect(need: Need): void {
    this.selectedNeed = need;
  }
  
  constructor(private needService: NeedService) { }
  needs: Need[] = [];
  getNeeds(): void {
    this.needService.getNeeds()
      .subscribe(needs => this.needs = needs);
  }
  ngOnInit(): void { 
    this.getNeeds();
  }
}
