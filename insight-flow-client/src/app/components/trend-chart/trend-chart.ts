import {
  Component,
  computed,
  effect,
  ElementRef,
  OnDestroy,
  signal,
  ViewChild
} from '@angular/core';

import { Chart, registerables } from 'chart.js';
import {
  MatCard,
  MatCardContent,
  MatCardHeader,
  MatCardSubtitle,
  MatCardTitle
} from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { NgIf, TitleCasePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

import { TrendPoint } from '../../models/trend-point.model';
import { TrendService } from '../../services/trend-service/trend-service';
import { AuthService } from '../../services/auth-service/auth-service';

Chart.register(...registerables);

@Component({
  selector: 'app-trend-chart',
  imports: [
    MatCard,
    MatCardHeader,
    MatIcon,
    MatCardTitle,
    MatCardSubtitle,
    MatCardContent,
    MatProgressSpinner,
    NgIf,
    TitleCasePipe
  ],
  templateUrl: './trend-chart.html',
  styleUrl: './trend-chart.css',
})
export class TrendChart implements OnDestroy {

  @ViewChild('chartCanvas') chartCanvas!: ElementRef<HTMLCanvasElement>;

  selectedTest = signal<string>('');
  trendData = signal<TrendPoint[]>([]);
  isLoading = signal(false);
  hasData = signal(false);
  errorMessage = signal('');

  private chart: Chart | null = null;

  constructor(
    private trendService: TrendService,
    public authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    effect(() => {
      const user = this.authService.currentUser();
      if (user?.userId) {
        // Read test name from route param
        const testName = this.route.snapshot.paramMap.get('testName') ?? '';
        this.selectedTest.set(testName);
        if (testName) {
          this.loadTrend(testName);
        }
      }
    });
  }

  // ======================
  // Computed stats
  // ======================
  latestValue = computed(() => {
    const data = this.trendData();
    return data.length ? data[data.length - 1] : null;
  });

  highestValue = computed(() => {
    const data = this.trendData();
    if (!data.length) return null;
    return data.reduce((max, p) => (p.value > max.value ? p : max));
  });

  lowestValue = computed(() => {
    const data = this.trendData();
    if (!data.length) return null;
    return data.reduce((min, p) => (p.value < min.value ? p : min));
  });

  trend = computed(() => {
    const data = this.trendData();
    if (data.length < 2) return 'stable';
    const first = data[0].value;
    const last = data[data.length - 1].value;
    const diff = ((last - first) / first) * 100;
    if (diff > 5) return 'rising';
    if (diff < -5) return 'falling';
    return 'stable';
  });

  // ======================
  // Load trend data
  // ======================
  loadTrend(testName: string) {
    this.isLoading.set(true);
    this.errorMessage.set('');

    const userId = this.authService.currentUser()?.userId;
    if (!userId) {
      this.errorMessage.set('User not logged in');
      return;
    }

    this.trendService.getTrend(testName, userId).subscribe({
      next: (points) => {
        this.trendData.set(points);
        this.isLoading.set(false);
        this.hasData.set(points.length > 0);

        if (points.length > 0) {
          setTimeout(() => this.renderChart(points), 100);
        }
      },
      error: (err) => {
        console.error('❌ Trend load error:', err);
        this.isLoading.set(false);
        this.errorMessage.set('Failed to load trend data.');
      }
    });
  }

  // ======================
  // Chart rendering
  // ======================
  private renderChart(points: TrendPoint[]) {
    if (this.chart) {
      this.chart.destroy();
      this.chart = null;
    }

    const canvas = this.chartCanvas?.nativeElement;
    if (!canvas) return;

    const labels = points.map(p =>
      new Date(p.date).toLocaleDateString('en-US', {
        month: 'short', day: 'numeric', year: '2-digit'
      })
    );

    const values = points.map(p => Number(p.value));
    const unit = points[0]?.unit ?? '';

    const pointColors = points.map(p => {
      if (p.colorCode === 'RED') return '#e53935';
      if (p.colorCode === 'YELLOW') return '#fb8c00';
      return '#43a047';
    });

    this.chart = new Chart(canvas, {
      type: 'line',
      data: {
        labels,
        datasets: [{
          label: `${this.selectedTest()} (${unit})`,
          data: values,
          borderColor: '#3f51b5',
          backgroundColor: 'rgba(63, 81, 181, 0.08)',
          borderWidth: 2.5,
          pointBackgroundColor: pointColors,
          pointBorderColor: pointColors,
          pointRadius: 6,
          pointHoverRadius: 9,
          tension: 0.35,
          fill: true
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          x: { grid: { display: false } },
          y: { grid: { color: 'rgba(0,0,0,0.06)' } }
        }
      }
    });
  }

  // ======================
  // UI helpers
  // ======================
  goBack() {
    this.router.navigate(['/dashboard']);
  }

  getStatusClass(colorCode: string): string {
    const map: Record<string, string> = {
      RED: 'status-red',
      YELLOW: 'status-yellow',
      GREEN: 'status-green'
    };
    return map[colorCode] ?? 'status-green';
  }

  getTrendIcon(): string {
    if (this.trend() === 'rising') return 'trending_up';
    if (this.trend() === 'falling') return 'trending_down';
    return 'trending_flat';
  }

  getTrendClass(): string {
    const t = this.trend();
    const latest = this.latestValue();
    if (!latest) return '';
    if (t === 'rising' && latest.status === 'high') return 'trend-bad';
    if (t === 'falling' && latest.status === 'low') return 'trend-bad';
    if (t === 'stable') return 'trend-stable';
    return 'trend-good';
  }

  ngOnDestroy() {
    this.chart?.destroy();
  }
}
