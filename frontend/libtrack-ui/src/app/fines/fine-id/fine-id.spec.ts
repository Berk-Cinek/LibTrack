import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FineId } from './fine-id';

describe('FineId', () => {
  let component: FineId;
  let fixture: ComponentFixture<FineId>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FineId],
    }).compileComponents();

    fixture = TestBed.createComponent(FineId);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
