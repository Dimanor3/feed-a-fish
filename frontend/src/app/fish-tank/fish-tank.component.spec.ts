import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FishTankComponent } from './fish-tank.component';

describe('FishTankComponent', () => {
  let component: FishTankComponent;
  let fixture: ComponentFixture<FishTankComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FishTankComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(FishTankComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
