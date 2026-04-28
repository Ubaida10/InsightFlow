import { TestBed } from '@angular/core/testing';

import { MedicalTooltipService } from './medical-tooltip-service';

describe('MedicalTooltipService', () => {
  let service: MedicalTooltipService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MedicalTooltipService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
