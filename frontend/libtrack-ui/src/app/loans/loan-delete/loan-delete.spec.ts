import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoanDelete } from './loan-delete';

describe('LoanDelete', () => {
  let component: LoanDelete;
  let fixture: ComponentFixture<LoanDelete>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoanDelete],
    }).compileComponents();

    fixture = TestBed.createComponent(LoanDelete);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
