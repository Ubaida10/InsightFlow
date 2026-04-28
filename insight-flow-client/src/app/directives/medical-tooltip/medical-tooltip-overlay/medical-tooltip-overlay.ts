import {Component, signal} from '@angular/core';
import {MatIcon} from '@angular/material/icon';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-medical-tooltip-overlay',
  imports: [
    MatIcon,
    MatProgressSpinner,
    NgIf
  ],
  templateUrl: './medical-tooltip-overlay.html',
  styleUrl: './medical-tooltip-overlay.css',
})
export class MedicalTooltipOverlay {
  term = signal('');
  explanation = signal('');
  loading = signal(false);
  visible = signal(false);
}
