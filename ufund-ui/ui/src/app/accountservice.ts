import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class AccountsService {
  constructor(
    private http: HttpClient) { }
  private needsURL = 'http://localhost:8080/accounts';
  login(username: string, password: string): Observable<string> {
    // todo: error handling + most of everything else
    return this.http.post(this.needsURL+'/login', {username: username, password: password}, {responseType: 'text'});
  }

  logout(username: string, key: string): void {
    this.http.post(this.needsURL+'/logout', username, {responseType: 'text', 'headers': {'key': key}});
  }

  test(username: string, key: string): Observable<string> {
    return this.http.get(this.needsURL+'/test/'+username, {responseType: 'text', 'headers': {'key': key}});
  }

  getInfo(username: string, key: string): Observable<string> {
    return this.http.get(this.needsURL+'/info/'+username, {responseType: 'text', 'headers': {'key': key}});
  }

  changeName(name: string, key: string): Observable<string> {
    return this.http.put('http://localhost:8080/user', {
      "id":localStorage.getItem("id"), 
      "username":name
    }
    , {responseType: 'text', 'headers': {'key': key}})
  }

  changePass(pass: string, key: string): Observable<string> {
    return this.http.put('http://localhost:8080/user', {
      "id":localStorage.getItem("id"), 
      "password":pass
    }
    , {responseType: 'text', 'headers': {'key': key}})
  }
}
