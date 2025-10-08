import { Component } from '@angular/core';
import { Need } from '../need';
import { NeedService } from '../needservice';
import { Observable, Subject, of } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, timeout } from 'rxjs/operators';

@Component({
  selector: 'app-cupboard',
  standalone: false,
  templateUrl: './cupboard.html',
  styleUrl: './cupboard.css'
})
export class Cupboard {
  needs$!: Observable<Need[]>;
  needs: Need[] = [];
  private searchTerms = new Subject<string>();
  selectedNeed?: Need;
  bText = "";
  isManager = false;
  v1 = "";
  v2 = "";
  v3 = "";
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
    this.initial = false;
    this.v1 = "All Needs";
    this.v2 = "Name";
    this.v3 = "Description";
    this.searchTerms.next(term);
  }

  constructor(private needService: NeedService) { }

  getNeeds(): void {
    this.needService.getNeeds()
      .subscribe(needs => this.needs = needs);
  }

  ngOnInit(): void {
    if (localStorage.getItem("role") == "manager") {
      this.bText = "Edit/Delete";
      this.isManager = true;
    } else if (localStorage.getItem("role") == "helper") {
      this.bText = "Add to Basket";
      this.isManager = false;
    }
    this.needs$ = this.searchTerms.pipe(
      distinctUntilChanged(),
      switchMap((term: string) => this.needService.searchNeeds(term)),
    );
    this.getNeeds();
  }
}
