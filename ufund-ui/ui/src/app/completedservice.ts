import { Injectable } from '@angular/core';
import { CompletedNeed } from './completedneed';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

/**
 * Does the HTTP requests for all completed need related tasks
 *
 * @author Anthony Ficalora
 */
@Injectable({
  providedIn: 'root'
})
export class CompletedService {

  constructor(private http: HttpClient) { }

  private completedNeedsURL = 'http://localhost:8080/cupboard/completed';
  private completedProportionURL = 'http://localhost:8080/cupboard/completion';

  /**
   * Handles HTTP request to get all Completed Needs
   *
   * @returns List of all Completed Needs
   */
  getCompletedNeeds(): Observable<CompletedNeed[]> {
    return this.http.get<CompletedNeed[]>(this.completedNeedsURL)
  }

  /**
   * Handles HTTP request to get all Completed Needs
   *
   * @param page Number of page (30 completed needs) to recieve, must be >= 1
   * @returns List of all Completed Needs
   */
  getCompletedNeedsPage(page: number): Observable<CompletedNeed[]> {
    return this.http.get<CompletedNeed[]>(this.completedNeedsURL + "/" + page)
  }

  /**
   * Handles HTTP request to get number of needs completed
   * 
   * @returns List with those numbers
   */
  getNumbers(): Observable<number[]> {
    return this.http.get<number[]>(this.completedNeedsURL + '/numbers');
  }

  /**
   * Handles HTTP request to get all Completed Needs
   *
   * @param search The search term
   * @returns Percent of needs matching the search term that have been completed (rounded to nearest percent)
   */
  getCompletionProportion(search: string): Observable<number> {
    return this.http.get<number>(this.completedProportionURL + "/" + search) 
  }
}