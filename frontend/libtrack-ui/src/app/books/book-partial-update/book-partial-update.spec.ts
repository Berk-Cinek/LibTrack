import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookPartialUpdate } from './book-partial-update';

describe('BookPartialUpdate', () => {
  let component: BookPartialUpdate;
  let fixture: ComponentFixture<BookPartialUpdate>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookPartialUpdate],
    }).compileComponents();

    fixture = TestBed.createComponent(BookPartialUpdate);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
