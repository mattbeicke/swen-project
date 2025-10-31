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

  /**
   * Sends a request to log in the given user.
   * @param username The user's username
   * @param password The user's password
   * @returns The API key returned by the request
   */
  login(username: string, password: string): Observable<string> {
    return this.http.post(this.accountURL + '/login', { username: username, password: password }, { responseType: 'text' });
  }

  /**
   * Logs out the given user.
   * @param username The user's username
   * @param key The current session's API key
   */
  logout(username: string, key: string): void {
    this.http.post(this.accountURL + '/logout', username, { responseType: 'text', 'headers': { 'key': key } });
  }

  /**
   * Tests if the current session's API key is valid.
   * @param username The user's username
   * @param key The current session's API key
   * @returns "API key is valid" if valid, else an empty string
   */
  test(username: string, key: string): Observable<string> {
    return this.http.get(this.accountURL + '/test/' + username, { responseType: 'text', 'headers': { 'key': key } });
  }

  /**
   * Gets the data about a user.
   * @param username The user's username
   * @param key The current session's API key
   * @returns The user's data with password removed
   */
  getInfo(username: string, key: string): Observable<User> {
    return this.http.get<User>(this.accountURL + '/info/' + username, { responseType: 'json', 'headers': { 'key': key } });
  }

  /**
   * Sends a request to create a User.
   * @param user A User object
   * @returns Returns the User object returned by the API, or null if failed
   */
  createAccount(user: User) {
    return this.http.post(this.userURL, user);
  }

  /**
   * Changes the name of the current User.
   * @param id The user's ID
   * @param name The user's username
   * @param key The current session's API key
   * @returns The string representation of the updated User object, or null if failed
   */
  changeName(id: string, name: string, key: string): Observable<string> {
    return this.http.put('http://localhost:8080/user', { "id": id, "username": name }, { responseType: 'text', 'headers': { 'key': key } })
  }

  /**
   * Changes the name of the current User.
   * @param id The user's ID
   * @param pass The user's password
   * @param key The current session's API key
   * @returns The string representation of the updated User object, or null if failed
   */
  changePass(id: string, pass: string, key: string): Observable<string> {
    return this.http.put('http://localhost:8080/user', { "id": id, "password": pass }, { responseType: 'text', 'headers': { 'key': key } })
  }

  /**
   * Deletes the current user.
   * @param pass The user's ID
   * @param key The current session's API key
   * @return Observable
   */
  deleteUser(id: string, key: string): Observable<string> {
    return this.http.delete('http://localhost:8080/user/' + id, { responseType: 'text', 'headers': { 'key': key } })
  }

  forgotPassword(username: string): Observable<string[]> {
    return this.http.get<string[]>(this.accountURL + '/' + username);
  }

  resetPassword(username: string, password: string): Observable<string> {
    return this.http.put(this.accountURL + '/' + username, { "password": password }, { responseType: 'text' });
  }
}
