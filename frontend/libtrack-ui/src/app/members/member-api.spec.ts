import { TestBed } from '@angular/core/testing';

import { MemberApi } from './member-api';

describe('MemberApi', () => {
  let service: MemberApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MemberApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
