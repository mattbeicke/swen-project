import { Component } from '@angular/core';
import { Need } from '../need';
import { NeedService } from '../needservice';
import { Router } from '@angular/router';

@Component({
  selector: 'app-cupboard',
  standalone: false,
  templateUrl: './basket-tab.html',
  styleUrl: './basket-tab.css'
})
export class BasketTab {
  constructor(private needService: NeedService, private router: Router) { }

  needs$: Need[] = [];
  selectedNeed?: Need;

  ngOnInit(): void {
    if (localStorage.getItem("role") == "manager") {
      this.router.navigate(['/cupboard']);
      alert("Administrator account do have access to a basket");
    }
    this.needService.viewBasket(+(localStorage.getItem("id") ?? ""), localStorage.getItem("key") ?? "")
      .subscribe(needs => this.needs$ = needs);
  }

  remove(need: Need): void {
    this.selectedNeed = need;
    this.needService.removeNeedFromBasket(+(localStorage.getItem("id") ?? ""), this.selectedNeed!.id, localStorage.getItem("key") ?? "")
      .subscribe({
        next: () => {
          alert("Removed " + this.selectedNeed?.name + " from your basket!");
          this.needService.viewBasket(+(localStorage.getItem("id") ?? ""), localStorage.getItem("key") ?? "")
            .subscribe(needs => this.needs$ = needs);
        },
        error: error => {
          switch (error.status) {
            case 401:
              alert("You are not authorized remove Needs from your basket");
              break;
            case 403:
              alert("You are not allowed remove Needs from your basket");
              break;
            case 404:
              alert("Need you are trying to remove a Need that no longer exists");
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

  checkout(): void {
    this.needService.checkout(+(localStorage.getItem("id") ?? ""), localStorage.getItem("key") ?? "")
      .subscribe({
        next: () => {
          alert("Successfully checked your items out!");
          this.needService.viewBasket(+(localStorage.getItem("id") ?? ""), localStorage.getItem("key") ?? "")
            .subscribe(needs => this.needs$ = needs);
        },
        error: error => {
          switch (error.status) {
            case 401:
              alert("You are not authorized checkout your basket");
              break;
            case 403:
              alert("You are not allowed checkout your basket");
              break;
            case 404:
              alert("You have no items in your basket!");
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
}
