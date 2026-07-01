import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoanId } from './loan-id';

describe('LoanId', () => {
  let component: LoanId;
  let fixture: ComponentFixture<LoanId>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoanId],
    }).compileComponents();

    fixture = TestBed.createComponent(LoanId);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
