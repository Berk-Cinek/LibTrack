import { Service, inject, signal, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Loan, CreateLoanRequest } from './/loan';

@Injectable({ providedIn: 'root'})
export class LoanApi {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/loans';

  getLoans() : Observable<LoanPage>{
    return  this.http.get<LoanPage>(this.baseUrl);
  }

  getLoanById(id: number) : Observable<Loan>{
    return this.http.get<Loan>(`${this.baseUrl}/${id}`)
  }

  createLoan(created: CreateLoanRequest): Observable<Loan> {
    return this.http.post<Loan>(this.baseUrl, created);
  }

  partialUpdate(id: number, changes: Partial<Loan>): Observable<Loan> {
    return this.http.patch<Loan>(`${this.baseUrl}/${id}`, changes);
  }

  deleteLoan(id: number): Observable<HttpResponse<void>> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { observe: 'response' });
  }
}

interface LoanPage{
  content: Loan[];
}

