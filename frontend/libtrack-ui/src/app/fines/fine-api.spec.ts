import { TestBed } from '@angular/core/testing';

import { FineApi } from './fines-api';

describe('FineApi', () => {
  let service: FineApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FineApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
