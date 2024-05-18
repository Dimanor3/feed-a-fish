import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { importProvidersFrom } from '@angular/core';
import { AppRoutesModule } from './app/app.routes';

bootstrapApplication(AppComponent, {
  providers: [importProvidersFrom(AppRoutesModule)],
});
