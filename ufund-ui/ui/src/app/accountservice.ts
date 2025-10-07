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

  logout(): void {
    // todo: not implemented
  }

  test(): boolean {
    return false; // todo: not implemented
  }
}
