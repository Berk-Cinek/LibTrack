import { Service, Injectable, inject} from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Member } from './member';

@Injectable({ providedIn: 'root'})
export class MemberApi {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/members';

  getMembers(): Observable<MemberPage>{
    return this.http.get<MemberPage>(this.baseUrl);
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
}
