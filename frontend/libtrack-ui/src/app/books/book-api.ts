import { Service , Injectable, inject} from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Book } from './book';


@Injectable({ providedIn: 'root'})
export class BookApi {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/books';

  getBooks(): Observable<BookPage>  {
    return this.http.get<BookPage>(this.baseUrl);
  }

  getBookById(id : number) : Observable<Book>{
    return this.http.get<Book>(`${this.baseUrl}/${id}`);
  }

  bookPartialUpdate(id: number, changes: Partial<Book>) : Observable<Book>{
    return this.http.patch<Book>(`${this.baseUrl}/${id}`, changes)
  }

  bookFullUpdate(id: number, changes: Partial<Book>) : Observable<Book>{
    return this.http.put<Book>(`${this.baseUrl}/${id}`, changes)
  }

  bookDelete(id: number) : Observable<HttpResponse<void>>{
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { observe: 'response' });
  }
}

interface BookPage{
  content: Book[];
}
