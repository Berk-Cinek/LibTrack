import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MemberId } from './member-id';

describe('MemberId', () => {
  let component: MemberId;
  let fixture: ComponentFixture<MemberId>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MemberId],
    }).compileComponents();

    fixture = TestBed.createComponent(MemberId);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
