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
  needs$!: Observable<Need[]>;
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
              alert("You are not authorized add Needs to your basket");
              break;
            case 403:
              alert("You are not allowed add Needs to your basket");
              break;
            case 404:
              alert("Need you are trying to add a Need that no longer exists");
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

    checkout(need: Need): void {
    this.selectedNeed = need;
    this.needService.checkout(+(localStorage.getItem("id") ?? ""), this.selectedNeed!.id, localStorage.getItem("key") ?? "")
      .subscribe({
        next: () => {
          alert("Checkout Complete");
        },
        error: error => {
          switch (error.status) {
            case 401:
              alert("You are not authorized add Needs to your basket");
              break;
            case 403:
              alert("You are not allowed add Needs to your basket");
              break;
            case 404:
              alert("Need you are trying to add a Need that no longer exists");
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

      view(need: Need): void {
    this.selectedNeed = need;
    this.needService.viewBasket(+(localStorage.getItem("id") ?? ""), this.selectedNeed!.id, localStorage.getItem("key") ?? "")
      .subscribe({
        next: () => {
          alert("Viewing Basket");
        },
        error: error => {
          switch (error.status) {
            case 401:
              alert("You are not authorized add Needs to your basket");
              break;
            case 403:
              alert("You are not allowed add Needs to your basket");
              break;
            case 404:
              alert("Need you are trying to add a Need that no longer exists");
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
    this.needService.getNeedsFromBasket()
      .subscribe(needs => this.needs$ = needs);
  }
}
