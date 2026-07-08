import { Service, Injectable, inject} from '@angular/core';
import { HttpClient, HttpResponse, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Member } from './member';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root'})
export class MemberApi {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/members`;

  getMembers(page = 0, size = 20, search = ''): Observable<MemberPage> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (search) {
      params = params.set('search', search);
    }
    return this.http.get<MemberPage>(this.baseUrl, { params });
  }

  getMemberById(id: number) : Observable<Member>{
    return this.http.get<Member>(`${this.baseUrl}/${id}`);
  }

  createMember(created: Partial<Member>) : Observable<Member>{
    return this.http.post<Member>(this.baseUrl, created);
  }

  fullUpdateMember(id: number, changes: Partial<Member>) : Observable<Member>{
    return this.http.put<Member>(`${this.baseUrl}/${id}`, changes);
  }

  PartialUpdateMember(id: number, changes: Partial<Member>) : Observable<Member>{
    return this.http.patch<Member>(`${this.baseUrl}/${id}`, changes);
  }

  memberDelete(id: number) : Observable<HttpResponse<void>>{
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { observe: 'response' });
  }
}

interface MemberPage{
  content: Member[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}
