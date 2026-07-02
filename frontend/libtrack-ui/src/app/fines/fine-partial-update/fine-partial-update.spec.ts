import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FinePartialUpdate } from './fine-partial-update';

describe('FinePartialUpdate', () => {
  let component: FinePartialUpdate;
  let fixture: ComponentFixture<FinePartialUpdate>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FinePartialUpdate],
    }).compileComponents();

    fixture = TestBed.createComponent(FinePartialUpdate);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
