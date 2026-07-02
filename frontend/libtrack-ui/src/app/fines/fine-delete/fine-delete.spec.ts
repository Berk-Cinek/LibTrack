import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FineDelete } from './fine-delete';

describe('FineDelete', () => {
  let component: FineDelete;
  let fixture: ComponentFixture<FineDelete>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FineDelete],
    }).compileComponents();

    fixture = TestBed.createComponent(FineDelete);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
