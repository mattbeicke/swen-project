import { Component } from '@angular/core';
import { Need } from '../need';
import { NeedService } from '../needservice';
import { Observable, Subject, of } from 'rxjs';
import { distinctUntilChanged, startWith, switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-cupboard',
  standalone: false,
  templateUrl: './basket-tab.html',
  styleUrl: './basket-tab.css'
})
export class BasketTab {
  needs$!: Need[];
  selectedNeed?: Need;

  remove(need: Need): void {
    this.selectedNeed = need;
    this.needService.removeNeedFromBasket(+(localStorage.getItem("id") ?? ""), this.selectedNeed!.id, localStorage.getItem("key") ?? "")
      .subscribe({
        next: () => {
          alert("Removed " + this.selectedNeed?.name + " from your basket!");
        },
        error: error => {
          switch (error.status) {
            case 401:
              alert("You are not authorized to remove a Need from this basket");
              break;
            case 403:
              alert("You are not allowed to remove a Need from a basket");
              break;
            case 404:
              alert("This Need is not in your basket");
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
    this.needService.checkout(+(localStorage.getItem("id") ?? ""), this.selectedNeed!.id, localStorage.getItem("key") ?? "")
      .subscribe({
        next: () => {
          alert("Checkout Complete");
        },
        error: error => {
          switch (error.status) {
            case 401:
              alert("You are not authorized to checkout this basket");
              break;
            case 403:
              alert("You are not allowed to checkout a basket");
              break;
            case 404:
              alert("There is nothing in your basket to checkout");
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

  constructor(private needService: NeedService) { }

  ngOnInit(): void {
    this.needService.viewBasket(+(localStorage.getItem("id") ?? ""), localStorage.getItem("key") ?? "")
      .subscribe(needs => this.needs$ = needs);
  }
}
