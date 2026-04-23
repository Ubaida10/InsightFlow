import { Injectable } from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UploadService {
  private apiUrl = `${environment.apiUrl}/api/upload`;

  constructor(private http: HttpClient) {}

  uploadFile(file: File):Observable<String> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(this.apiUrl, formData, {responseType: 'text'});
  }
}
