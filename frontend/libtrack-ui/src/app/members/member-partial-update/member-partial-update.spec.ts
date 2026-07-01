import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MemberPartialUpdate } from './member-partial-update';

describe('MemberPartialUpdate', () => {
  let component: MemberPartialUpdate;
  let fixture: ComponentFixture<MemberPartialUpdate>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MemberPartialUpdate],
    }).compileComponents();

    fixture = TestBed.createComponent(MemberPartialUpdate);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
