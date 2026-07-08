import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Fine } from './fine';
import { environment } from '../../environments/environment';


@Injectable({ providedIn: 'root' })
export class FineApi {

  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/fines`;

  getFines(): Observable<FinePage> {
    return this.http.get<FinePage>(this.baseUrl);
  }

  getFinesAdmin(page = 0, size = 20, search = ''): Observable<FinePage> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (search) params = params.set('search', search);
    return this.http.get<FinePage>(this.baseUrl, { params });
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

export interface FinePage {
  content: Fine[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}
