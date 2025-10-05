import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BasketTab } from './basket-tab';

describe('BasketTab', () => {
  let component: BasketTab;
  let fixture: ComponentFixture<BasketTab>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BasketTab]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BasketTab);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
