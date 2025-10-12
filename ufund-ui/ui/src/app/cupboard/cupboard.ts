import { Component } from '@angular/core';
import { Need } from '../need';
import { NeedService } from '../needservice';
import { Observable, Subject, of } from 'rxjs';
import { distinctUntilChanged, startWith, switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-cupboard',
  standalone: false,
  templateUrl: './cupboard.html',
  styleUrl: './cupboard.css'
})
export class Cupboard {
  needs$!: Observable<Need[]>;
  private searchTerms = new Subject<string>();
  selectedNeed?: Need;
  bText = "";
  isManager = false;
  initial = true;

  onSelect(need: Need): void {
    this.selectedNeed = need;
    if (localStorage.getItem("role") == "manager") {
      //TODO: open edit/delete modal
    } else if (localStorage.getItem("role") == "helper") {
      //TODO: add to basket
    }
  }

  create(): void {
    // verify a users role then do:
    if (localStorage.getItem("role") != "manager") {
      alert("You are not authorized to create new Needs");
      return;
    }
    let name = prompt("Enter need name", "Cans of soup");
    if (name == null || name == "") {
      return;
    }
    let description = prompt("Enter " + name + "'s description", "5 Minestrone, 6 Chicken Noodle");
    if (description == null || description == "") {
      return;
    }
    this.needService.createNeed({ name, description } as Need, localStorage.getItem("key") ?? "")
      .subscribe({
        next: () => {
          this.search("");
        },
        error: error => {
          switch (error.status) {
            case 401:
              alert("You are not authorized to create new Needs");
              break;
            case 404:
              alert("Internal Error");
              break;
            case 500:
              alert("Internal server error");
              break;
            default:
              alert("Unknown error, is server online?");
          }
        }
      });

  }

  search(term: string): void {
    this.searchTerms.next(term);
  }

  constructor(private needService: NeedService) { }

  ngOnInit(): void {
    if (localStorage.getItem("role") == "manager") {
      this.bText = "Edit/Delete";
      this.isManager = true;
    } else if (localStorage.getItem("role") == "helper") {
      this.bText = "Add to Basket";
      this.isManager = false;
    }

    this.needService.getNeeds()
      .subscribe(needs => {
        this.needs$ = this.searchTerms.pipe(
          distinctUntilChanged(),
          switchMap((term: string) => this.needService.searchNeeds(term)),
          startWith(needs),
        );
      })
  }
}
