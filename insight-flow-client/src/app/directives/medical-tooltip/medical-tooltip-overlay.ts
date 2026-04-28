import {
  ApplicationRef,
  ComponentRef,
  createComponent,
  Directive,
  ElementRef,
  EnvironmentInjector,
  HostListener, Input, OnDestroy
} from '@angular/core';
import {Subscription} from 'rxjs';
import {MedicalTooltipService} from '../../services/medical-tooltip/medical-tooltip-service';
import { MedicalTooltipOverlay as MedicalTooltipOverlayComponent } from './medical-tooltip-overlay/medical-tooltip-overlay';

@Directive({
  selector: '[appMedicalTooltipOverlay]',
})
export class MedicalTooltipOverlay implements OnDestroy {
  @Input('medicalTooltip') term = '';

  private overlayRef: ComponentRef<MedicalTooltipOverlayComponent> | null = null;
  private sub: Subscription | null = null;
  private hideTimer: any;
  private HOVER_DELAY_MS = 400; // wait before showing — avoids flicker on fast mouse movement

  constructor(
    private el: ElementRef,
    private tooltipService: MedicalTooltipService,
    private appRef: ApplicationRef,
    private injector: EnvironmentInjector
  ) {}

  @HostListener('mouseenter')
  onMouseEnter() {
    clearTimeout(this.hideTimer);

    this.hideTimer = setTimeout(() => {
      this.showTooltip();
    }, this.HOVER_DELAY_MS);
  }

  @HostListener('mouseleave')
  onMouseLeave() {
    clearTimeout(this.hideTimer);
    this.hideTooltip();
  }

  private showTooltip() {
    if (!this.term?.trim()) return;
    this.createOverlay();
    this.positionOverlay();
    this.loadExplanation();
  }

  private createOverlay() {
    // Reuse existing overlay if already created
    if (this.overlayRef) {
      this.overlayRef.instance.visible.set(true);
      return;
    }

    this.overlayRef = createComponent(MedicalTooltipOverlayComponent, {
      environmentInjector: this.injector
    });

    this.appRef.attachView(this.overlayRef.hostView);
    document.body.appendChild(this.overlayRef.location.nativeElement);
  }

  private positionOverlay() {
    if (!this.overlayRef) return;

    const rect = this.el.nativeElement.getBoundingClientRect();
    const el = this.overlayRef.location.nativeElement.querySelector('.tooltip-panel') as HTMLElement;

    if (!el) return;

    const tooltipWidth = 320;
    const gap = 10;

    // Position above the element by default
    let top = rect.top - gap;
    let left = rect.left + rect.width / 2 - tooltipWidth / 2;

    // Keep within viewport horizontally
    if (left < 8) left = 8;
    if (left + tooltipWidth > window.innerWidth - 8) {
      left = window.innerWidth - tooltipWidth - 8;
    }

    // Flip below if not enough space above
    const tooltipHeight = 120;
    if (top - tooltipHeight < 8) {
      top = rect.bottom + gap;
    } else {
      top = rect.top - tooltipHeight - gap;
    }

    el.style.top = `${top}px`;
    el.style.left = `${left}px`;
  }

  private loadExplanation() {
    if (!this.overlayRef) return;

    const instance = this.overlayRef.instance;
    instance.term.set(this.term);
    instance.explanation.set('');
    instance.loading.set(true);
    instance.visible.set(true);

    this.sub?.unsubscribe();
    this.sub = this.tooltipService.getExplanation(this.term).subscribe(explanation => {
      if (this.overlayRef) {
        instance.explanation.set(explanation);
        instance.loading.set(false);
      }
    });
  }

  private hideTooltip() {
    if (this.overlayRef) {
      this.overlayRef.instance.visible.set(false);
    }
  }

  ngOnDestroy() {
    this.sub?.unsubscribe();
    clearTimeout(this.hideTimer);

    if (this.overlayRef) {
      this.appRef.detachView(this.overlayRef.hostView);
      this.overlayRef.destroy();
      this.overlayRef = null;
    }
  }
}
