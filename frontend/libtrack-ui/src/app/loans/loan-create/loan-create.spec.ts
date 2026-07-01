import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoanCreate } from './loan-create';

describe('LoanCreate', () => {
  let component: LoanCreate;
  let fixture: ComponentFixture<LoanCreate>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoanCreate],
    }).compileComponents();

    fixture = TestBed.createComponent(LoanCreate);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
