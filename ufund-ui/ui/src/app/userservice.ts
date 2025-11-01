import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from './user';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(private http: HttpClient) { }

  private userURL = 'http://localhost:8080/user';

  getUsers(key: string): Observable<User[]> {
    return this.http.get<User[]>(this.userURL, { responseType: 'json', 'headers': { 'key': key } });
  }

  searchUsers(term: string, key: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.userURL}/?name=${term}`, { responseType: 'json', 'headers': { 'key': key } });
  }
}
