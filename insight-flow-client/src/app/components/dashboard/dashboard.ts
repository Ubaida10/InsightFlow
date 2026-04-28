import {Component, signal} from '@angular/core';
import {LabResult} from '../../models/lab-result.model';
import {LabReport} from '../../models/lab-report.model';
import {MatCard, MatCardContent, MatCardHeader, MatCardSubtitle, MatCardTitle} from '@angular/material/card';
import {MatIcon} from '@angular/material/icon';
import {Upload} from '../upload/upload';
import {NgIf, TitleCasePipe} from '@angular/common';
import {MatTable} from '@angular/material/table';
import {MatTooltip} from '@angular/material/tooltip';

@Component({
  selector: 'app-dashboard',
  imports: [
    MatCard,
    MatCardHeader,
    MatIcon,
    MatCardTitle,
    MatCardSubtitle,
    MatCardContent,
    Upload,
    NgIf,
    MatTable,
    TitleCasePipe,
    MatTooltip
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard {
  report = signal<LabReport | null>(null);

  displayedColumns = ['test', 'value', 'referenceRange', 'status', 'urgency', 'flag'];

  onReportReady(report: LabReport) {
    this.report.set(report);
  }

  get flaggedResults(): LabResult[] {
    return this.report()?.results.filter(r => r.flagged) ?? [];
  }

  get normalResults(): LabResult[] {
    return this.report()?.results.filter(r => !r.flagged) ?? [];
  }

  getStatusClass(result: LabResult): string {
    return result.colorCode?.toLowerCase() ?? 'green';
  }

  getStatusIcon(result: LabResult): string {
    if (result.colorCode === 'RED') return 'warning';
    if (result.colorCode === 'YELLOW') return 'info';
    return 'check_circle';
  }

  getUrgencyClass(level: string): string {
    const map: Record<string, string> = {
      'URGENT': 'urgency-urgent',
      'ELEVATED': 'urgency-elevated',
      'ROUTINE': 'urgency-routine'
    };
    return map[level] ?? 'urgency-routine';
  }
}
