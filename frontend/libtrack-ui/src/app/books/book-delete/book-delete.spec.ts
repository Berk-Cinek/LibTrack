import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookDelete } from './book-delete';

describe('BookDelete', () => {
  let component: BookDelete;
  let fixture: ComponentFixture<BookDelete>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookDelete],
    }).compileComponents();

    fixture = TestBed.createComponent(BookDelete);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
