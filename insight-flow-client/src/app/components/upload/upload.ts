import { Component } from '@angular/core';
import { signal, output } from '@angular/core';
import {CommonModule, NgIf} from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import {MatIcon, MatIconModule} from '@angular/material/icon';
import {MatProgressBar, MatProgressBarModule} from '@angular/material/progress-bar';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { UploadService } from '../../services/upload-service/upload-service';
import { LabReport } from '../../models/lab-report.model';

@Component({
  selector: 'app-upload',
  imports: [
    MatIcon,
    MatProgressBar,
    NgIf
  ],
  templateUrl: './upload.html',
  styleUrl: './upload.css',
})
export class Upload {
  reportReady = output<LabReport>();

  selectedFile = signal<File | null>(null);
  isLoading = signal(false);
  isDragOver = signal(false);

  constructor(
    private uploadService: UploadService,
    private snackBar: MatSnackBar
  ) {}

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.selectedFile.set(input.files[0]);
    }
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    this.isDragOver.set(true);
  }

  onDragLeave() {
    this.isDragOver.set(false);
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    this.isDragOver.set(false);
    const file = event.dataTransfer?.files[0];
    if (file) this.selectedFile.set(file);
  }

  onUpload() {
    const file = this.selectedFile();
    if (!file) return;

    this.isLoading.set(true);
    this.uploadService.uploadFile(file).subscribe({
      next: (report) => {
        this.isLoading.set(false);
        this.selectedFile.set(null);
        this.reportReady.emit(report);
        this.snackBar.open('Report analyzed successfully', 'Close', { duration: 3000 });
      },
      error: (err) => {
        this.isLoading.set(false);
        this.snackBar.open('Upload failed: ' + err.message, 'Close', { duration: 5000 });
      }
    });
  }
}
