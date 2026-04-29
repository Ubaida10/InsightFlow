import { Injectable } from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {TrendPoint} from '../../models/trend-point.model';

@Injectable({
  providedIn: 'root',
})
export class TrendService {
  private apiUrl = `${environment.apiUrl}/api/trends`;

  constructor(private http: HttpClient) {}

  getTrend(testName: string, userId : string = 'anonymous'): Observable<TrendPoint[]> {
    return this.http.get<TrendPoint[]>(`${this.apiUrl}/${encodeURIComponent(testName)}?userId=${userId || 'anonymous'}`);
  }

  getAvailableTests(userId: String = 'anonymous'): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/available-tests?userId=${userId || 'anonymous'}`);
  }
}
