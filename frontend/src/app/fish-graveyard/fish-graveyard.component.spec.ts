import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FishGraveyardComponent } from './fish-graveyard.component';

describe('FishGraveyardComponent', () => {
  let component: FishGraveyardComponent;
  let fixture: ComponentFixture<FishGraveyardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FishGraveyardComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(FishGraveyardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
