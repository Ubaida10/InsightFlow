import { Injectable } from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {LabReport} from '../../models/lab-report.model';

@Injectable({
  providedIn: 'root',
})
export class UploadService {
  private apiUrl = `${environment.apiUrl}/api/upload`;

  constructor(private http: HttpClient) {}

  uploadFile(file: File, userId: string = 'anonymous'):Observable<LabReport> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('userId', userId);
    return this.http.post<LabReport>(this.apiUrl, formData);
  }
}
