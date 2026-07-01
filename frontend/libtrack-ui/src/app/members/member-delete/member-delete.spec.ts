import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MemberDelete } from './member-delete';

describe('MemberDelete', () => {
  let component: MemberDelete;
  let fixture: ComponentFixture<MemberDelete>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MemberDelete],
    }).compileComponents();

    fixture = TestBed.createComponent(MemberDelete);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
