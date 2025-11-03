import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from './user';

/**
 * Does the HTTP requests for users tab
 *
 * @author Matthew Beicke
 */
@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(private http: HttpClient) { }

  private userURL = 'http://localhost:8080/user';

  /**
   * Handles HTTP request to get all Users
   * 
   * @param key api key of user running this function
   * @returns list of all users
   */
  getUsers(key: string): Observable<User[]> {
    return this.http.get<User[]>(this.userURL, { responseType: 'json', 'headers': { 'key': key } });
  }

  /**
   * Handles HTTP request to search for Users
   * 
   * @param term what to search for
   * @param key api key of user searching
   * @returns list of all users whose username contains the term
   */
  searchUsers(term: string, key: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.userURL}/?username=${term}`, { responseType: 'json', 'headers': { 'key': key } });
  }

  getMaxUsers(): Observable<number> {
    return this.http.get<number>(this.userURL + '/max');
  }

  getTopNUsers(n: number): Observable<User[]> {
    return this.http.get<User[]>(this.userURL + '/top/?n=' + n, { responseType: 'json' });
  }
}
