import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

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
