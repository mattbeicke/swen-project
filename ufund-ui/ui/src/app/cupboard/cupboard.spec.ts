import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Cupboard } from './cupboard';

describe('Cupboard', () => {
  let component: Cupboard;
  let fixture: ComponentFixture<Cupboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [Cupboard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Cupboard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
