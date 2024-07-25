import { ResolveFn } from '@angular/router';
import { FishService } from './fish.service';
import { inject } from '@angular/core';
import { FishStatus } from './fish-status.model';



export const fishResolveResolver: ResolveFn<FishStatus[]> = (route, state) => {
  const fishService = inject(FishService)
  fishService.getDeadFish()
  return fishService.fishDead
};
