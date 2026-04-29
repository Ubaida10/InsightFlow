export interface TrendPoint {
  date: string;
  value: number;
  unit: string;
  status: string;
  colorCode: 'GREEN' | 'YELLOW' | 'RED';
  reportId: string;
}
