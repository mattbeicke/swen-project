import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from './user';

@Injectable({
  providedIn: 'root'
})
export class AccountsService {
  constructor(private http: HttpClient) { }

  private accountURL = 'http://localhost:8080/accounts';
  private userURL = 'http://localhost:8080/user';

  login(username: string, password: string): Observable<string> {
    return this.http.post(this.accountURL + '/login', { username: username, password: password }, { responseType: 'text' });
  }

  logout(username: string, key: string): void {
    this.http.post(this.accountURL + '/logout', username, { responseType: 'text', 'headers': { 'key': key } });
  }

  test(username: string, key: string): Observable<string> {
    return this.http.get(this.accountURL + '/test/' + username, { responseType: 'text', 'headers': { 'key': key } });
  }

  getInfo(username: string, key: string): Observable<string> {
    return this.http.get(this.accountURL + '/info/' + username, { responseType: 'text', 'headers': { 'key': key } });
  }

  createAccount(user: User) {
    return this.http.post(this.userURL, user);
  }

  changeName(user: User, key: string): Observable<User> {
    return this.http.put<User>('http://localhost:8080/user', user, { 'headers': { 'key': key } });
  }

  changePass(user: User, key: string): Observable<User> {
    return this.http.put<User>('http://localhost:8080/user', user, { 'headers': { 'key': key } });
  }

  deleteUser(id: string, key: string): Observable<string> {
    return this.http.delete('http://localhost:8080/user/' + id, { responseType: 'text', 'headers': { 'key': key } })
  }
}
