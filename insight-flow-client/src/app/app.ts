import { Component, signal, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NgIf],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('insight-flow-client');

  private http = inject(HttpClient);
  serverMessage = signal('');

  testConnection() {
    this.http.get(`${environment.apiUrl}/api/hello`, { responseType: 'text' })
      .subscribe({
        next: (response) => this.serverMessage.set(response),
        error: (error) => this.serverMessage.set('Error: ' + error.message)
      });
  }
}
