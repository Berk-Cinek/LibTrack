import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginWidget } from './login-widget';

describe('LoginWidget', () => {
  let component: LoginWidget;
  let fixture: ComponentFixture<LoginWidget>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginWidget],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginWidget);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
