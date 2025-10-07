import { Component } from '@angular/core';
import { Need } from '../need';
import { NeedService } from '../needservice';

import {
  NgIf,
  NgFor,
  UpperCasePipe,
} from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-cupboard',
  standalone: false,
  templateUrl: './cupboard.html',
  styleUrl: './cupboard.css'
})
export class Cupboard {
  selectedNeed?: Need;
  bText = "";

  onSelect(need: Need): void {
    this.selectedNeed = need;
    if (localStorage.getItem("role") == "manager") {
      //TODO: open edit/delete modal
    } else {
      //TODO: add to basket
    }
  }

  constructor(private needService: NeedService) { }
  needs: Need[] = [];
  getNeeds(): void {
    this.needService.getNeeds()
      .subscribe(needs => this.needs = needs);
  }
  ngOnInit(): void {
    this.getNeeds();
    if (localStorage.getItem("role") == "manager") {
      this.bText = "Edit/Delete";
    } else {
      this.bText = "Add to Basket";
    }
  }
}
