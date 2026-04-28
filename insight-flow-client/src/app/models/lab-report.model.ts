import {LabResult} from './lab-result.model';

export interface LabReport {
  id: string;
  patientName: string;
  reportDate: string;
  originalFileName: string;
  uploadedAt: string;
  analyzedAt: string;
  disclaimer: string;
  results: LabResult[];
}
