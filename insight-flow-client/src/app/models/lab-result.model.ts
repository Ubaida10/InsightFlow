export interface LabResult {
  test: string;
  value: number;
  unit: string;
  referenceRange: string;
  status: 'normal' | 'high' | 'low';
  colorCode: 'GREEN' | 'YELLOW' | 'RED';
  urgencyLevel: 'ROUTINE' | 'ELEVATED' | 'URGENT';
  flagged: boolean;
  flagReason: string | null;
}
