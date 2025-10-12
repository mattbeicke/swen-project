import { Injectable } from '@angular/core';
import { Need } from './need';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class NeedService {
  constructor(private http: HttpClient) { }

  private needsURL = 'http://localhost:8080/cupboard';
  private managerURL = 'http://localhost:8080/manager';

  getNeeds(): Observable<Need[]> {
    return this.http.get<Need[]>(this.needsURL)
  }
  searchNeeds(term: string): Observable<Need[]> {
    return this.http.get<Need[]>(`${this.needsURL}/?name=${term}`);
  }
  createNeed(need: Need, key: string): Observable<Need> {
    return this.http.post<Need>(this.managerURL + '/add', need, { responseType: 'json', 'headers': { 'key': key }});
  }
}
