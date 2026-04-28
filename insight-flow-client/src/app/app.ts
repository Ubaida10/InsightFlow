import { Component } from '@angular/core';

import {Dashboard} from './components/dashboard/dashboard';

@Component({
  selector: 'app-root',
  imports: [Dashboard],
  templateUrl: './app.html',
  standalone: true,
  styleUrl: './app.css'
})
export class App {}
