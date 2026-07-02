import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FineList } from './fine-list';

describe('FineList', () => {
  let component: FineList;
  let fixture: ComponentFixture<FineList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FineList],
    }).compileComponents();

    fixture = TestBed.createComponent(FineList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
