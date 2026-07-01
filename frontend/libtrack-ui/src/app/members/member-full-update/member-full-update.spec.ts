import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MemberFullUpdate } from './member-full-update';

describe('MemberFullUpdate', () => {
  let component: MemberFullUpdate;
  let fixture: ComponentFixture<MemberFullUpdate>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MemberFullUpdate],
    }).compileComponents();

    fixture = TestBed.createComponent(MemberFullUpdate);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
