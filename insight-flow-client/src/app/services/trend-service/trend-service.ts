import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TrendPoint } from '../../models/trend-point.model';

@Injectable({
  providedIn: 'root',
})
export class TrendService {
  private baseUrl = `${environment.apiUrl}/api/trends`;

  constructor(private http: HttpClient) {}

  getTrend(testName: string, userId: string = 'anonymous'): Observable<TrendPoint[]> {
    return this.http.get<TrendPoint[]>(`${this.baseUrl}/${encodeURIComponent(testName)}?userId=${userId}`);
  }

  getAvailableTests(userId: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/available-tests?userId=${userId}`);
  }
}
