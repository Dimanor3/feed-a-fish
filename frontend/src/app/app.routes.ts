import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { fishResolveResolver } from './shared/fish-resolve.resolver';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./fish-tank/fish-tank.component').then(
        (mod) => mod.FishTankComponent
      ),
  },
  {
    path: 'fish-graveyard',
    resolve: {deadFish: fishResolveResolver},
    loadComponent: () =>
      import('./fish-graveyard/fish-graveyard.component').then(
        (mod) => mod.FishGraveyardComponent
      ),
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutesModule {}
