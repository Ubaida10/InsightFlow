import {Component, signal} from '@angular/core';
import {AuthService} from '../../../services/auth-service/auth-service';
import {Router, RouterLink} from '@angular/router';
import {MatCard, MatCardContent, MatCardHeader} from '@angular/material/card';
import {MatIcon} from '@angular/material/icon';
import {MatProgressBar} from '@angular/material/progress-bar';
import {NgIf} from '@angular/common';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {FormsModule} from '@angular/forms';
import {MatButton, MatIconButton} from '@angular/material/button';

@Component({
  selector: 'app-login',
  imports: [
    MatCardHeader,
    MatCard,
    MatIcon,
    MatCardContent,
    MatProgressBar,
    NgIf,
    MatFormField,
    MatLabel,
    MatInput,
    FormsModule,
    MatIconButton,
    MatButton,
    RouterLink
  ],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  email = '';
  password = '';
  isLoading = signal(false);
  errorMessage = signal('');
  hidePassword = signal(true);

  constructor(private authService: AuthService, private router: Router) {}

  onLogin() {
    if (!this.email || !this.password) {
      this.errorMessage.set('Please fill in all fields.');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');

    this.authService.login({ email: this.email, password: this.password }).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: () => {
        this.isLoading.set(false);
        this.errorMessage.set('Invalid email or password.');
      }
    });
  }
}
