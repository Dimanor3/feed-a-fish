import { TestBed } from '@angular/core/testing';
import { ResolveFn } from '@angular/router';
import { fishResolveResolver } from './fish-resolve.resolver';
import { FishStatus } from './fish-status.model';



describe('fishResolveResolver', () => {
  const executeResolver: ResolveFn<FishStatus[]> = (...resolverParameters) => 
      TestBed.runInInjectionContext(() => fishResolveResolver(...resolverParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeResolver).toBeTruthy();
  });
});
