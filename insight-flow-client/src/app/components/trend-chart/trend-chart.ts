import {Component, computed, ElementRef, OnDestroy, OnInit, signal, ViewChild} from '@angular/core';
import {Chart, registerables} from 'chart.js';
import {MatCard, MatCardContent, MatCardHeader, MatCardSubtitle, MatCardTitle} from '@angular/material/card';
import {MatIcon} from '@angular/material/icon';
import {MatFormField, MatLabel} from '@angular/material/input';
import {MatOption, MatSelect} from '@angular/material/select';
import {NgForOf, NgIf, TitleCasePipe} from '@angular/common';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {TrendPoint} from '../../models/trend-point.model';
import {TrendService} from '../../services/trend-service/trend-service';

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
    MatFormField,
    MatLabel,
    MatSelect,
    MatOption,
    NgForOf,
    MatProgressSpinner,
    NgIf,
    TitleCasePipe
  ],
  templateUrl: './trend-chart.html',
  styleUrl: './trend-chart.css',
})
export class TrendChart implements OnInit, OnDestroy {
  @ViewChild('chartCanvas') chartCanvas!: ElementRef<HTMLCanvasElement>;

  availableTests = signal<string[]>([]);
  selectedTest = signal<string>('');
  trendData = signal<TrendPoint[]>([]);
  isLoading = signal(false);
  hasData = signal(false);
  errorMessage = signal('');

  private chart: Chart | null = null;

  // Computed summary stats
  latestValue = computed(() => {
    const data = this.trendData();
    return data.length ? data[data.length - 1] : null;
  });

  highestValue = computed(() => {
    const data = this.trendData();
    if (!data.length) return null;
    return data.reduce((max, p) => p.value > max.value ? p : max);
  });

  lowestValue = computed(() => {
    const data = this.trendData();
    if (!data.length) return null;
    return data.reduce((min, p) => p.value < min.value ? p : min);
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

  constructor(private trendService: TrendService) {}

  ngOnInit() {
    this.loadAvailableTests();
  }

  loadAvailableTests() {
    this.trendService.getAvailableTests().subscribe({
      next: (tests) => {
        this.availableTests.set(tests);
        if (tests.length) {
          this.selectedTest.set(tests[0]);
          this.loadTrend(tests[0]);
        }
      },
      error: () => this.errorMessage.set('Failed to load available tests.')
    });
  }

  onTestChange(testName: string) {
    this.selectedTest.set(testName);
    this.loadTrend(testName);
  }

  loadTrend(testName: string) {
    this.isLoading.set(true);
    this.errorMessage.set('');

    this.trendService.getTrend(testName).subscribe({
      next: (points) => {
        this.trendData.set(points);
        this.isLoading.set(false);
        this.hasData.set(points.length > 0);

        if (points.length > 0) {
          setTimeout(() => this.renderChart(points), 0);
        }
      },
      error: () => {
        this.isLoading.set(false);
        this.errorMessage.set('Failed to load trend data.');
      }
    });
  }

  private renderChart(points: TrendPoint[]) {
    // Destroy existing chart before re-rendering
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

    const values = points.map(p => p.value);
    const unit = points[0]?.unit ?? '';

    // Color each point based on status
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
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (ctx) => {
                const point = points[ctx.dataIndex];
                return [
                  ` Value: ${ctx.parsed.y} ${unit}`,
                  ` Status: ${point.status.toUpperCase()}`,
                ];
              }
            },
            backgroundColor: '#1a237e',
            titleColor: '#90caf9',
            bodyColor: 'rgba(255,255,255,0.9)',
            padding: 12,
            cornerRadius: 8
          }
        },
        scales: {
          x: {
            grid: { display: false },
            ticks: { color: '#757575', font: { size: 11 } }
          },
          y: {
            grid: { color: 'rgba(0,0,0,0.06)' },
            ticks: {
              color: '#757575',
              font: { size: 11 },
              callback: (val) => `${val} ${unit}`
            }
          }
        }
      }
    });
  }

  getStatusClass(colorCode: string): string {
    const map: Record<string, string> = {
      'RED': 'status-red',
      'YELLOW': 'status-yellow',
      'GREEN': 'status-green'
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
    // Rising is bad if status is high, good if status is low
    if (t === 'rising' && latest.status === 'high') return 'trend-bad';
    if (t === 'falling' && latest.status === 'low') return 'trend-bad';
    if (t === 'stable') return 'trend-stable';
    return 'trend-good';
  }

  ngOnDestroy() {
    this.chart?.destroy();
  }
}
