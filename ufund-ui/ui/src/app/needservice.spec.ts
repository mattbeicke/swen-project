import { TestBed } from '@angular/core/testing';

import { NeedService } from './needservice';

describe('NeedService', () => {
  let service: NeedService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NeedService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
