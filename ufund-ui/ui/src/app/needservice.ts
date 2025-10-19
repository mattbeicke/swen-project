import { Injectable } from '@angular/core';
import { Need } from './need';
import { User } from './user';
import { Manager } from './manager';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

/**
 * Does the HTTP requests for all need related tasks
 *
 * @author Matthew Beicke
 */
@Injectable({
  providedIn: 'root'
})
export class NeedService {

  constructor(private http: HttpClient) { }

  private needsURL = 'http://localhost:8080/cupboard';
  private managerURL = 'http://localhost:8080/manager';
  private userURL = 'http://localhost:8080/user';

  /**
   * Handles HTTP request to get all Needs
   *
   * @returns List of all Needs
   */
  getNeeds(): Observable<Need[]> {
    return this.http.get<Need[]>(this.needsURL)
  }

  /**
   * Handles HTTP request to get all Needs that contain the search term
   *
   * @param term What to search by
   * @returns List of all Needs that contain the term
   */
  searchNeeds(term: string): Observable<Need[]> {
    return this.http.get<Need[]>(`${this.needsURL}/?name=${term}`);
  }

  /**
   * Handles HTTP request to create a new need
   *
   * @param need Need to create
   * @param key Manager's API Key
   * @returns Need that was created
   */
  createNeed(need: Need, key: string): Observable<Need> {
    return this.http.post<Need>(this.managerURL + '/add', need, { responseType: 'json', 'headers': { 'key': key } });
  }

  /**
   * Handles HTTP request to edit a need
   *
   * @param need Need to edit
   * @param key Manager's API Key
   * @returns Need that was edited
   */
  editNeed(need: Need, key: string): Observable<Need> {
    return this.http.put<Need>(this.managerURL + "/edit", need, { responseType: 'json', 'headers': { 'key': key } })
  }

  /**
   * Handles HTTP request to delete a need
   *
   * @param id id of need to delete
   * @param key Manager's API Key
   * @returns Manager account (as we had to return something?)
   */
  deleteNeed(id: number, key: string): Observable<Manager> {
    return this.http.post<Manager>(this.managerURL + "/delete/" + id, 0, { responseType: 'json', 'headers': { 'key': key } })
  }

  /**
   * Handles HTTP request to add a need to a helpers basket
   *
   * @param userID id of user to add need to
   * @param needID id of need to add to user
   * @param key Helper's API Key
   * @returns User account (as we had to return something?)
   */
  addNeedtoBasket(userID: number, needID: number, key: string): Observable<User> {
    return this.http.post<User>(this.userURL + "/basket/add", { "userID": userID, "needID": needID }, { responseType: 'json', 'headers': { 'key': key } });
  }

  removeNeedFromBasket(userID: number, needID: number, key: string) {
    return this.http.post<User>(this.userURL + "/basket/remove", { "userID": userID, "needID": needID}, {responseType: 'json', 'headers': { 'key': key}});
  }

  checkout(userID: number, needID: number, key: string) {
    return this.http.post<User>(this.userURL + "/basket/checkout/{id}", { "userID": userID, "needID": needID}, {responseType: 'json', 'headers': { 'key': key}});
  }

  viewBasket(userID: number, needID: number, key: string) {
    return this.http.post<User>(this.userURL + "/basket/{id}", { "userID": userID, "needID": needID}, {responseType: 'json', 'headers': { 'key': key}});
  }


}
