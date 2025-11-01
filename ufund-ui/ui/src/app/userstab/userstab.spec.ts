import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UsersTab } from './userstab';

describe('UsersTab', () => {
  let component: UsersTab;
  let fixture: ComponentFixture<UsersTab>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UsersTab]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UsersTab);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
