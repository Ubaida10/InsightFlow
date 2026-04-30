import {Component, signal} from '@angular/core';
import {LabResult} from '../../models/lab-result.model';
import {LabReport} from '../../models/lab-report.model';
import {MatCard, MatCardContent, MatCardHeader, MatCardSubtitle, MatCardTitle} from '@angular/material/card';
import {MatIcon} from '@angular/material/icon';
import {Upload} from '../upload/upload';
import {NgIf, TitleCasePipe} from '@angular/common';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatRow,
  MatRowDef,
  MatTable
} from '@angular/material/table';
import {MatTooltip} from '@angular/material/tooltip';
import {MedicalTooltipOverlay} from '../../directives/medical-tooltip/medical-tooltip-overlay';
import {AuthService} from '../../services/auth-service/auth-service';
import {Router} from '@angular/router';

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
    MatTooltip,
    MedicalTooltipOverlay,
    MatRowDef,
    MatColumnDef,
    MatHeaderCell,
    MatCell,
    MatCellDef,
    MatHeaderCellDef,
    MatHeaderRowDef,
    MatHeaderRow,
    MatRow
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard {
  report = signal<LabReport | null>(null);

  displayedColumns = ['test', 'value', 'referenceRange', 'status', 'urgency', 'flag'];

  constructor(public authService: AuthService, private router: Router) {}

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

  trackTest(testName: string) {
    this.router.navigate(['/trends', testName]);
  }
}
