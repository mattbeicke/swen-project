import { TestBed } from '@angular/core/testing';

import { Completedservice } from './completedservice';

describe('Completedservice', () => {
  let service: Completedservice;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Completedservice);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
