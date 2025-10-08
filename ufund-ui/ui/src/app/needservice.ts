import { Injectable } from '@angular/core';
import { Need } from './need';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class NeedService {
  constructor(
    private http: HttpClient) { }
  private needsURL = 'http://localhost:8080/cupboard';
  getNeeds(): Observable<Need[]> {
    return this.http.get<Need[]>(this.needsURL)
  }
  searchNeeds(term: string): Observable<Need[]> {
    return this.http.get<Need[]>(`${this.needsURL}/?name=${term}`);
  }
}
