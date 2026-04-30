import {Injectable, signal} from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {AuthResponse} from '../../models/auth-response.model';
import {RegisterRequest} from '../../models/register-request.model';
import {Observable, tap} from 'rxjs';
import {LoginRequest} from '../../models/login-request.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/api/auth`;

  currentUser = signal<AuthResponse | null>(this.loadFromStorage());

  constructor(private http: HttpClient, private router: Router) {}

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request).pipe(
      tap(response => this.saveUser(response))
    );
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
      tap(response=> this.saveUser(response))
    );
  }

  logout() {
    localStorage.removeItem('insightflow-user');
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  getToken():string | null {
    return this.currentUser()?.token || null;
  }

  isLoggedIn(): boolean {
    const user = this.currentUser();
    if (!user || !user.token) return false;

    try {
      const payload = JSON.parse(atob(user.token.split('.')[1]));
      const isValid = payload.exp * 1000 > Date.now();

      if (!isValid) {
        this.logout();
      }

      return isValid;
    } catch {
      this.logout(); 
      return false;
    }
  }

  private saveUser(response: AuthResponse) {
    localStorage.setItem('insightflow-user', JSON.stringify(response));
    this.currentUser.set(response);
  }

  private loadFromStorage(): AuthResponse | null {
    const data = localStorage.getItem('insightflow-user');
    return data ? JSON.parse(data) : null;
  }
}
