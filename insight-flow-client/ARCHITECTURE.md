# Frontend Request Architecture - InsightFlow Client

## Overview

The Angular frontend follows a **layered architecture** to send requests to the backend server. This separation of concerns makes the code more maintainable, testable, and reusable.

## Architecture Flow

```
┌─────────────────────────────────────────────────────────────┐
│                     USER INTERFACE (app.html)               │
│  - File input field                                          │
│  - Upload button                                             │
│  - Display selected file name                                │
│  - Display response message                                  │
└────────────────────────────┬────────────────────────────────┘
                             │
                             │ Event: (change), (click)
                             │
┌────────────────────────────▼────────────────────────────────┐
│               COMPONENT LOGIC (app.ts)                       │
│  - Captures user events                                      │
│  - Manages UI state (signals)                                │
│  - Calls service methods                                     │
│  - Handles response/errors                                   │
└────────────────────────────┬────────────────────────────────┘
                             │
                             │ Calls method
                             │
┌────────────────────────────▼────────────────────────────────┐
│              SERVICE LAYER (upload-service.ts)               │
│  - Encapsulates API logic                                    │
│  - Builds HTTP requests                                      │
│  - Uses environment configuration                            │
│  - Returns Observable                                        │
└────────────────────────────┬────────────────────────────────┘
                             │
                             │ Uses
                             │
┌────────────────────────────▼────────────────────────────────┐
│         HTTP CLIENT & ENVIRONMENT                            │
│  - HttpClient (Angular's HTTP module)                        │
│  - environment.ts (API URL configuration)                    │
│  - Sends actual HTTP POST request                            │
└────────────────────────────┬────────────────────────────────┘
                             │
                             │ HTTP POST
                             ▼
                      BACKEND SERVER
                   (Spring Boot API)
```

## Detailed Explanation

### 1. **User Interface Layer** (`app.html`)

```html
<input type="file" accept=".pdf, image/*" (change)="onFileSelected($event)" />
<button (click)="onUpload()" [disabled]="!selectedFile()">Upload</button>
```

- User selects a file → triggers `(change)` event
- User clicks upload → triggers `(click)` event
- Events are bound to component methods using Angular event binding `(event)="method()"`

### 2. **Component Layer** (`app.ts`)

```typescript
export class App {
  selectedFile = signal<File | null>(null);
  uploadService: UploadService;

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      this.selectedFile.set(file);  // Store file in signal
    }
  }

  onUpload() {
    const file = this.selectedFile();
    this.uploadService.uploadFile(file).subscribe({
      next: (res) => {this.responseMessage.set(res)},
      error: (err) => {this.responseMessage.set(`Error: ${err.message}`)}
    });
  }
}
```

**Responsibilities:**
- Captures the selected file from the input element
- Stores the file in a signal (reactive state)
- Calls the service method `uploadService.uploadFile(file)`
- Subscribes to the Observable returned by the service
- Updates the UI with the response or error

### 3. **Service Layer** (`upload-service.ts`)

```typescript
@Injectable({
  providedIn: 'root',
})
export class UploadService {
  private apiUrl = `${environment.apiUrl}/api/upload`;

  constructor(private http: HttpClient) {}

  uploadFile(file: File): Observable<String> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(this.apiUrl, formData, {responseType: 'text'});
  }
}
```

**Responsibilities:**
- Encapsulates all HTTP logic for file uploads
- Builds the FormData object required for file upload
- Uses the injected `HttpClient` to make the POST request
- Uses `environment.apiUrl` for the backend URL (configurable)
- Returns an Observable that the component can subscribe to

### 4. **Environment Configuration** (`environment.ts`)

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'
};
```

- Centralizes API URL configuration
- Allows different URLs for dev and production environments
- Service uses this to construct the full API endpoint

## Data Flow Step-by-Step

### When user uploads a file:

```
1. USER ACTION
   ├─ User selects file
   └─ Triggers (change) event on <input>

2. COMPONENT CAPTURES
   ├─ onFileSelected() method receives Event
   ├─ Extracts File object from input.files
   └─ Stores in selectedFile signal

3. USER CLICKS UPLOAD
   ├─ Button click triggers (click) event
   └─ Calls onUpload() method

4. COMPONENT CALLS SERVICE
   ├─ Retrieves file from selectedFile signal
   ├─ Calls uploadService.uploadFile(file)
   └─ Subscribes to returned Observable

5. SERVICE PREPARES REQUEST
   ├─ Creates FormData object
   ├─ Appends file to FormData
   ├─ Gets API URL from environment: 'http://localhost:8080/api/upload'
   └─ Returns Observable with HTTP POST request

6. HTTP REQUEST SENT
   ├─ Angular's HttpClient sends POST request
   ├─ Includes CORS headers
   ├─ Sends FormData as request body
   └─ Waits for response from backend

7. RESPONSE RECEIVED
   ├─ Backend (Spring Boot) processes request
   ├─ Returns success/error message
   └─ Observable emits response

8. COMPONENT HANDLES RESPONSE
   ├─ next: Response received successfully
   │  └─ Updates responseMessage signal
   └─ error: Error occurred
      └─ Updates responseMessage with error
```

## Why This Architecture?

### **Separation of Concerns**
- **Component**: Manages UI logic and user interactions
- **Service**: Handles API communication
- **Environment**: Centralizes configuration

### **Reusability**
- The service can be used by multiple components
- If you need to upload files elsewhere, just inject the service

### **Maintainability**
- API URL changes in one place (environment.ts)
- Backend endpoint changes in one place (service)
- Easy to test each layer independently

### **Scalability**
- Easy to add interceptors (for authentication, logging)
- Easy to add error handling
- Easy to add caching

## Observable & Reactive Programming

```typescript
uploadService.uploadFile(file).subscribe({
  next: (res) => { /* Handle success */ },
  error: (err) => { /* Handle error */ },
  complete: () => { /* Optional: Handle completion */ }
});
```

- **Service returns Observable**: Asynchronous stream of data
- **Component subscribes**: Listens for response
- **RxJS operators**: Can be added for transformation, filtering, etc.

## Example: If You Want to Add Logging

You could add it in the service:

```typescript
uploadFile(file: File): Observable<String> {
  const formData = new FormData();
  formData.append('file', file);
  
  console.log('Uploading file:', file.name);
  
  return this.http.post(this.apiUrl, formData, {responseType: 'text'})
    .pipe(
      tap(res => console.log('Upload successful:', res)),
      catchError(err => {
        console.error('Upload failed:', err);
        throw err;
      })
    );
}
```

No changes needed in the component!

## Key Takeaways

1. **Three-tier request architecture**: UI → Component → Service → HTTP Client
2. **Environment-based configuration**: API URL from `environment.ts`
3. **Services for API logic**: Reusable, maintainable, testable
4. **Observables for async**: RxJS handles asynchronous operations
5. **Separation of concerns**: Each file has a specific responsibility

This architecture makes the frontend scalable and maintainable as your application grows!

