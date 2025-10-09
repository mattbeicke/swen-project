import { Component } from '@angular/core';
import { Need } from '../need';
import { NeedService } from '../needservice';
import { Observable, Subject, of } from 'rxjs';
import { debounceTime, distinctUntilChanged, startWith, subscribeOn, switchMap, timeout } from 'rxjs/operators';

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
