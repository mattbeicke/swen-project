import { Injectable } from '@angular/core';
import { Need } from './need';
import { User } from './user';
import { Manager } from './manager';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class NeedService {
  constructor(private http: HttpClient) { }

  private needsURL = 'http://localhost:8080/cupboard';
  private managerURL = 'http://localhost:8080/manager';
  private userURL = 'http://localhost:8080/user';

  getNeeds(): Observable<Need[]> {
    return this.http.get<Need[]>(this.needsURL)
  }

  searchNeeds(term: string): Observable<Need[]> {
    return this.http.get<Need[]>(`${this.needsURL}/?name=${term}`);
  }

  createNeed(need: Need, key: string): Observable<Need> {
    return this.http.post<Need>(this.managerURL + '/add', need, { responseType: 'json', 'headers': { 'key': key } });
  }

  editNeed(need: Need, key: string): Observable<Need> {
    return this.http.post<Need>(this.managerURL + "/edit", need, { responseType: 'json', 'headers': { 'key': key } })
  }

  deleteNeed(id: number, key: string): Observable<Manager> {
    return this.http.post<Manager>(this.managerURL + "/delete/" + id, 0, { responseType: 'json', 'headers': { 'key': key } })
  }

  addNeedtoBasket(userID: number, needID: number, key: string): Observable<User> {
    return this.http.post<User>(this.userURL + "/basket/add", { "userID": userID, "needID": needID }, { responseType: 'json', 'headers': { 'key': key } });
  }
}