import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoanPartialUpdate } from './loan-partial-update';

describe('LoanPartialUpdate', () => {
  let component: LoanPartialUpdate;
  let fixture: ComponentFixture<LoanPartialUpdate>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoanPartialUpdate],
    }).compileComponents();

    fixture = TestBed.createComponent(LoanPartialUpdate);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
