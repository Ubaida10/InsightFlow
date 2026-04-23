import { Component, signal, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';
import {NgIf} from '@angular/common';
import {UploadService} from './services/upload-service/upload-service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NgIf],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  selectedFile = signal<File | null>(null);
  selectedFileName = signal<string>('');
  responseMessage = signal<String>('');

  constructor(private uploadService: UploadService) {}

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      this.selectedFile.set(file);
      this.selectedFileName.set(file.name);
    }
  }

  onUpload() {
    const file = this.selectedFile();

    if(!file) return;

    this.uploadService.uploadFile(file).subscribe({
      next: (res) => {this.responseMessage.set(res)},
      error: (err) => {this.responseMessage.set(`Error: ${err.message}`)}
    });
  }
}
