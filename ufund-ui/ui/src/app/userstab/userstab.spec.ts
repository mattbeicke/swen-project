import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Userstab } from './userstab';

describe('Userstab', () => {
  let component: Userstab;
  let fixture: ComponentFixture<Userstab>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [Userstab]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Userstab);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
