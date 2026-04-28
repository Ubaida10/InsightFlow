import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MedicalTooltipOverlay } from './medical-tooltip-overlay';

describe('MedicalTooltipOverlay', () => {
  let component: MedicalTooltipOverlay;
  let fixture: ComponentFixture<MedicalTooltipOverlay>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MedicalTooltipOverlay],
    }).compileComponents();

    fixture = TestBed.createComponent(MedicalTooltipOverlay);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
