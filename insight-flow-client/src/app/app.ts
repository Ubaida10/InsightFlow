import { Component } from '@angular/core';

import {Dashboard} from './components/dashboard/dashboard';
import {RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [Dashboard, RouterOutlet],
  templateUrl: './app.html',
  standalone: true,
  styleUrl: './app.css'
})
export class App {}
