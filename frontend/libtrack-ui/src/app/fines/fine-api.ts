import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Fine } from './fine';



@Injectable({ providedIn: 'root' })
export class FineApi {

  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/fines';

  getFines(): Observable<FinePage> {
    return this.http.get<FinePage>(this.baseUrl);
  }

  getFineById(id: number): Observable<Fine> {
    return this.http.get<Fine>(`${this.baseUrl}/${id}`);
  }

  partialUpdate(id: number, changes: Partial<Fine>): Observable<Fine> {
    return this.http.patch<Fine>(`${this.baseUrl}/${id}`, changes);
  }

  deleteFine(id: number): Observable<HttpResponse<void>> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { observe: 'response' });
  }

}

interface FinePage {
  content: Fine[];
}
