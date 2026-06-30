import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookFullUpdate } from './book-full-update';

describe('BookFullUpdate', () => {
  let component: BookFullUpdate;
  let fixture: ComponentFixture<BookFullUpdate>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookFullUpdate],
    }).compileComponents();

    fixture = TestBed.createComponent(BookFullUpdate);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
