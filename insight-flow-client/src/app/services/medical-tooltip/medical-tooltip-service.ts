import { Injectable } from '@angular/core';
import {catchError, Observable, of, tap} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class MedicalTooltipService {
  private cache = new Map<string, string>();
  private apiUrl = `${environment.apiUrl}/api/explain`;

  constructor(private http: HttpClient) {}

  getExplanation(term: string): Observable<string> {
    const key = term.trim().toLowerCase();

    // Return cached result instantly — no API call
    if (this.cache.has(key)) {
      return of(this.cache.get(key)!);
    }

    return this.http.get(`${this.apiUrl}/${encodeURIComponent(term)}`, {
      responseType: 'text'
    }).pipe(
      tap(explanation => this.cache.set(key, explanation)),
      catchError(() => of('Unable to load explanation. Please try again.'))
    );
  }
}
